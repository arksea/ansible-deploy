package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.manage.dao.*;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.ansible.deploy.api.manage.msg.GetOperationJobHistory;
import net.arksea.ansible.deploy.api.manage.msg.GetUserApps;
import net.arksea.ansible.deploy.api.operator.dao.OperationJobDao;
import net.arksea.ansible.deploy.api.operator.dao.OperationTokenDao;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import net.arksea.restapi.RestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * @author xiaohaixing
 */
@Component
public class AppService {

    @Autowired
    private AppDao appDao;
    @Autowired
    private VersionDao versionDao;
    @Autowired
    private AppVarDefineDao appVarDefineDao;
    @Autowired
    AppVarDao appVarDao;
    @Autowired
    PortDao portDao;
    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    OperationTokenDao operationTokenDao;
    @Autowired
    PortTypeDao portTypeDao;
    @Autowired
    OperationJobDao operationJobDao;
    @Autowired
    AppOperationDao appOperationDao;
    @Autowired
    UserDao userDao;
    @Autowired
    VersionService versionService;
    @Autowired
    PortsService portsService;

    ZoneId zoneId = ZoneId.of("+8");

    @Transactional
    public boolean deleteApp(long appId) {
        App app = appDao.findOne(appId);
        if (app==null) {
            return false;
        }
        for (Version v: app.getVersions()) {
            for (Host h: v.getTargetHosts()) {
                versionDao.removeHost(v.getId(), h.getId());
            }
            v.getTargetHosts().clear();
            versionDao.delete(v.getId());
        }
        app.getVersions().clear();
        List<Port> ports = portDao.findByAppId(app.getId());
        for (Port p: ports) {
            portTypeDao.incRestCount(1, p.getTypeId());
        }
        portDao.releaseByAppId(appId);
        operationTokenDao.deleteByAppId(appId);
        appDao.delete(appId);
        return true;
    }

    public App createAppTemplate(String appTypeName) {
        AppType type = appTypeDao.findByName(appTypeName);
        App app = new App();
        app.setAppType(type);
        Version ver = versionService.createVersionTemplate(appTypeName);
        ver.setName("online");
        app.setVersions(new HashSet<>());
        app.getVersions().add(ver);
        app.setVars(new HashSet<>());
        Iterable<AppVarDefine> defines = appVarDefineDao.findByAppTypeId(type.getId());
        for (AppVarDefine def : defines) {
            if (def.getPortType() == null) { //端口类型变量保存时自动分配，无需返回给客户端进行编辑
                AppVariable var = new AppVariable();
                var.setValue(def.getDefaultValue());
                var.setName(def.getName());
                var.setIsPort(def.getPortType() != null);
                app.getVars().add(var);
            }
        }
        return app;
    }

    @Transactional
    public App save(final App app) {
        final boolean isNewAction = app.getId() == null;
        if (isNewAction) {
            App a = appDao.findByApptag(app.getApptag());
            if (a != null) {
                throw new RestException("应用名重复");
            }
        }
        try {
            if (!isNewAction) {
                App old = appDao.findOne(app.getId());
                portsService.updatePortVariables(app.getId(), old.getVars(), app.getVars());
            }
            App saved = appDao.save(app);
            if (isNewAction) {//分配端口
                List<AppVarDefine> defines = appVarDefineDao.findByAppTypeId(app.getAppType().getId());
                portsService.initPortVariables(app.getId(), saved.getVars(), defines, AppVariable::new);
            }
            long id = saved.getId();
            for (final AppVariable v : app.getVars()) {
                v.setAppId(id);
                appVarDao.save(v);
            }
            if (isNewAction) {
                for (final Version v : app.getVersions()) {
                    v.setAppId(id);
                    versionService.saveVersion(v);
                }
                createOperationToken(saved.getId());
            }
            return saved;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("保存应用失败", ex);
        }
    }

    @Transactional
    private void createOperationToken(long appId) {
        OperationToken token = new OperationToken();
        token.setAppId(appId);
        token.setReleased(true);
        operationTokenDao.save(token);
    }

    public App findOne(final Long id) {
        return appDao.findOne(id);
    }

    @Transactional
    public void deletedById(final long id) {
        deleteApp(id);
    }

    public GetUserApps.Response findUserApps(GetUserApps.Request msg) {
        int offset = (msg.page < 1 ? 0 : msg.page - 1) * msg.pageSize;
        List<App> apps;
        long total;
        if (StringUtils.isBlank(msg.nameSearch)) {
            apps = appDao.findPageByUserId(msg.userId, offset, msg.pageSize);
            total = appDao.getUserAppsCount(msg.userId);
        } else {
            String like = "%" + msg.nameSearch + "%";
            apps = appDao.findPageByUserId(msg.userId, like, offset, msg.pageSize);
            total = appDao.getUserAppsCount(msg.userId, like);
        }
        long totalPages = total/msg.pageSize;
        if (total % msg.pageSize > 0) {
            totalPages++;
        }
        return new GetUserApps.Response(total, totalPages, apps);
    }

    public List<App> findNotInGroup() {
        return appDao.findAllGroupIsNull();
    }

    public GetOperationJobHistory.Response findOperationJobInfos(GetOperationJobHistory.Request msg) {
        int page = msg.page < 1 ? 0 : msg.page - 1;
        Pageable pageable = new PageRequest(page, msg.pageSize, Sort.Direction.DESC, "id");
        App app = appDao.findOne(msg.appId);
        List<Long> statusOperationIds = new LinkedList<>();
        if (app != null) {
            Iterable<AppOperation> ops = appOperationDao.findByAppTypeId(app.getAppType().getId());
            for (AppOperation op : ops) {
                if (op.getType() == AppOperationType.STATUS) {
                    statusOperationIds.add(op.getId());
                }
            }
        }
        Specification<OperationJob> specification = new Specification<OperationJob>() {
            @Override
            public Predicate toPredicate(Root<OperationJob> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("appId").as(Long.class), msg.appId));
                if (StringUtils.isNotBlank(msg.startTime)) {
                    ZonedDateTime z = ZonedDateTime.parse(msg.startTime, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime l = z.withZoneSameInstant(zoneId).toLocalDateTime();
                    Timestamp t = Timestamp.valueOf(l);
                    predicateList.add(cb.greaterThanOrEqualTo(root.get("startTime").as(Timestamp.class), t));
                }
                if (StringUtils.isNotBlank(msg.endTime)) {
                    ZonedDateTime z = ZonedDateTime.parse(msg.endTime, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime l = z.withZoneSameInstant(zoneId).toLocalDateTime().plusDays(1);
                    Timestamp t = Timestamp.valueOf(l);
                    predicateList.add(cb.lessThan(root.get("startTime").as(Timestamp.class), t));
                }
                if (StringUtils.isNotBlank(msg.operator)) {
                    User user = userDao.findOneByName(msg.operator);
                    if (user == null) {
                        throw new ServiceException("未找到用户["+msg.operator+"]");
                    }
                    predicateList.add(cb.equal(root.get("operatorId").as(Long.class), user.getId()));
                }
                for (Long opId: statusOperationIds) { //过滤状态查询操作
                    predicateList.add(cb.notEqual(root.get("operationId").as(Long.class), opId));
                }
                return cb.and(predicateList.toArray(new Predicate[0]));
            }
        };
        Page<OperationJob> jobs = operationJobDao.findAll(specification, pageable);
        return new GetOperationJobHistory.Response(jobs.getTotalElements(),jobs.getTotalPages(), jobsToInfos(jobs));
    }

    private List<GetOperationJobHistory.OperationJobInfo> jobsToInfos(Iterable<OperationJob> jobs) {
        List<GetOperationJobHistory.OperationJobInfo> infos = new LinkedList<>();
        Map<Long, AppOperation> opMap = new HashMap<>();
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Version> verMap = new HashMap<>();
        for (OperationJob j : jobs) {
            AppOperation op = opMap.computeIfAbsent(j.getOperationId(), id -> appOperationDao.findOne(id));
            User user = userMap.computeIfAbsent(j.getOperatorId(), id -> userDao.findOne(id));
            String version;
            if (j.getVersionId() == null) {
                version = "/";
            } else {
                Version ver = verMap.computeIfAbsent(j.getVersionId(), id -> versionDao.findOne(id));
                version = ver==null? "/" : ver.getName();
            }
            String operation = op == null ? "/" : op.getName();
            String operator = user == null ? "/" : user.getName();
            infos.add(new GetOperationJobHistory.OperationJobInfo(j.getId(), operation, operator, j.getTriggerId(), version, j.getStartTime(), j.getEndTime()));
        }
        return infos;
    }
}
