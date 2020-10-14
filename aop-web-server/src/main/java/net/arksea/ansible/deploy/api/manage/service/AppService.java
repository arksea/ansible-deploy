package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.manage.dao.*;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.ansible.deploy.api.operator.dao.OperationTokenDao;
import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

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
    GroupVarDao groupVarDao;
    @Autowired
    PortDao portDao;
    @Autowired
    PortsStatDao portStatDao;
    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    OperationTokenDao operationTokenDao;

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

    public App createAppTemplate(String appTypeName) {
        AppType type = appTypeDao.findByName(appTypeName);
        App app = new App();
        app.setAppType(type);
        Version ver = new Version();
        ver.setName("Online");
        ver.setRepository("trunk");
        ver.setRevision("HEAD");
        ver.setExecOpt("");
        ver.setTargetHosts(new HashSet<>());
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
            createOperationToken(saved.getId());
            return saved;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("保存应用失败", ex);
        }
    }

    private void setPortsAndVars(App app) {
        List<AppVarDefine> defines = appVarDefineDao.findByAppTypeId(app.getAppType().getId());
        int portCount = 0;
        for (AppVarDefine def: defines) {
            if (def.getPortType() != null) {
                portCount++;
                PortType portType = def.getPortType();
                int n = portDao.assignForAppByTypeId(app.getId(), portType.getId());
                if (n == 0) {
                    throw new ServiceException("'"+portType.getName()+"'端口可用数不够，请联系管理员");
                }
                portStatDao.incRestCount(-1, portType.getId());
            }
        }
        List<Port> ports = portDao.findByAppId(app.getId());
        if (ports.size() < portCount) {
            throw new ServiceException("没有足够端口可供分配，请联系管理员");
        } else if (ports.size() > portCount) {
            throw new ServiceException("断言失败：分配端口逻辑错误");
        }
        for (AppVarDefine def: defines) {
            if (def.getPortType() != null) {
                PortType portType = def.getPortType();
                for (Port p : ports) {
                    if (portType.getId() == p.getTypeId()) {
                        AppVariable var = new AppVariable();
                        var.setAppId(app.getId());
                        var.setIsPort(true);
                        var.setName(def.getName());
                        var.setValue(Integer.toString(p.getValue()));
                        app.getVars().add(var);
                        ports.remove(p);
                        break;
                    }
                }
            }
        }
    }

    private void createOperationToken(long appId) {
        OperationToken token = new OperationToken();
        token.setAppId(appId);
        token.setReleased(true);
        operationTokenDao.save(token);
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
