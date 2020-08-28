package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.GroupVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author xiaohaixing
 */
@Component
@Transactional
public class AppService {

    @Autowired
    private AppDao appDao;

    public App save(final App app) {
        for (final GroupVar v : app.getVars()) {
            v.setApp(app);
        }
        return appDao.save(app);
    }

    public App findOne(final Long id) {
        return appDao.findOne(id);
    }

    public void delete(final App app) {
        appDao.delete(app);
    }
}
