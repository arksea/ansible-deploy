package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.GroupVar;
import net.arksea.ansible.deploy.api.manage.entity.Version;
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

    @Transactional
    public App save(final App app) {
        if (app.getId() == null) {
            App a = appDao.findByApptag(app.getApptag());
            if (a != null) {
                throw new RestException("应用名重复");
            }
        }
        try {
            for (final GroupVar v : app.getVars()) {
                v.setApp(app);
            }
            return appDao.save(app);
        } catch (Exception ex) {
            throw new RestException("保存应用失败", ex);
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

    @Transactional
    public Version createVersion(long appId, Version version) {
        App app = new App();
        app.setId(appId);
        version.setApp(app);
        Version saved = versionDao.save(version);
        return saved;
    }
}
