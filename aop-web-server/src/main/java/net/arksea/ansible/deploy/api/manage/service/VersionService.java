package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/14
 */
@Component
public class VersionService {
    @Autowired
    VersionDao versionDao;

    @Transactional
    public void addHosts(long versionId, List<Long> hosts) {
        for (Long h: hosts) {
            versionDao.addHost(versionId, h);
        }
    }
}
