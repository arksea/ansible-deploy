package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@Component
public class OperationsService {
    @Autowired
    AppOperationDao operationDao;

    @Transactional
    public AppOperation save(AppOperation operation) {
        return operationDao.save(operation);
    }

    public Iterable<AppOperation> getAll() {
        return operationDao.findAll();
    }

    public Iterable<AppOperation> getByAppTypeId(long appTypeId) {
        return operationDao.findByAppTypeId(appTypeId);
    }
}
