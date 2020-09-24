package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.*;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

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
    GroupVarDao groupVarDao;
    @Autowired
    PortDao portDao;
    @Autowired
    PortsStatDao portStatDao;
    @Autowired
    AppTypeDao appTypeDao;

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
            portStatDao.incRestCount(1, p.getTypeId());
        }
        portDao.releaseByAppId(appId);
        appDao.delete(appId);
        return true;
    }

    public App createAppForEdit(String appTypeName) {
        AppType type = appTypeDao.findByName(appTypeName);
        App app = new App();
        app.setAppType(type);
        app.setVersions(new HashSet<>());
        app.setVars(new HashSet<>());
        Version ver = new Version();
        return null;
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
            App saved = appDao.save(app);
            if (isNewAction) {//分配端口
                setPortsAndVars(saved);
            }
            long id = saved.getId();
            for (final AppVariable v : app.getVars()) {
                v.setAppId(id);
                groupVarDao.save(v);
            }
            if (isNewAction) {
                for (final Version v : app.getVersions()) {
                    v.setAppId(id);
                    versionDao.save(v);
                }
            }
            return saved;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("保存应用失败", ex);
        }
    }

    private void setPortsAndVars(App app) {
        List<AppPort> cfg = AppPortsConfiger.get(app.getAppType().getName());
        for (AppPort p : cfg) {
            int n = portDao.assignForAppByTypeId(app.getId(), p.portType.getId());
            if (n == 0) {
                throw new ServiceException("'"+p.portType.getName()+"'端口可用数不够，请联系管理员");
            }
            portStatDao.incRestCount(-1, p.portType.getId());
        }
        List<Port> ports = portDao.findByAppId(app.getId());
        if (ports.size() < cfg.size()) {
            throw new ServiceException("没有足够端口可供分配，请联系管理员");
        } else if (ports.size() > cfg.size()) {
            throw new ServiceException("断言失败：分配端口逻辑错误");
        }
        for (AppPort c: cfg) {
            for (Port p: ports) {
                if (c.portType.getId() == p.getTypeId()) {
                    AppVariable var = new AppVariable();
                    var.setAppId(app.getId());
                    var.setIsPort(true);
                    var.setName(c.name);
                    var.setValue(Integer.toString(p.getValue()));
                    app.getVars().add(var);
                    ports.remove(p);
                    break;
                }
            }
        }
    }

    public App findOne(final Long id) {
        return appDao.findOne(id);  //不能使用findOne方法，结果不正确
    }

    @Transactional
    public void updateDeletedById(final long id, boolean deleted) {
        //appDao.updateDeletedById(id, deleted);
        deleteApp(id); //todo: 直接删除，测试完成后改回来
    }

    public List<App> findByUserId(long userId) {
        return appDao.findByUserId(userId);
    }

    public List<App> findNotInGroup() {
        return appDao.findAllGroupIsNull();
    }

}
