package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.GroupVarDao;
import net.arksea.ansible.deploy.api.manage.dao.PortDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    GroupVarDao groupVarDao;
    @Autowired
    PortDao portDao;

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
            for (final GroupVar v : app.getVars()) {
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
        } catch (Exception ex) {
            throw new RestException("保存应用失败", ex);
        }
    }

    private void setPortsAndVars(App app) {
        List<AppPort> cfg = AppPortsConfiger.get(app.getApptype());
        for (AppPort p : cfg) {
            portDao.setAppIdByTypeId(app.getId(), p.portType);
        }
        List<Port> ports = portDao.findByAppId(app.getId());
        for (AppPort c: cfg) {
            for (Port p: ports) {
                if (c.portType == p.getTypeId()) {
                    GroupVar var = new GroupVar();
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
        return appDao.findOne(id);
    }

    @Transactional
    public void updateDeletedById(final long id, boolean deleted) {
        appDao.updateDeletedById(id, deleted);
    }

    public List<App> findByUserId(long userId) {
        return appDao.findByUserId(userId);
    }

    public List<App> findNotInGroup() {
        return appDao.findAllGroupIsNull();
    }

}
