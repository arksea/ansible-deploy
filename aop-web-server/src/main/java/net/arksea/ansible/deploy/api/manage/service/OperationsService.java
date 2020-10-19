package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppOperationCodeDao;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@Component
public class OperationsService {
    @Autowired
    AppOperationDao operationDao;
    @Autowired
    AppOperationCodeDao operationCodeDao;

    @Transactional
    public AppOperation saveOperation(AppOperation operation) {
        return operationDao.save(operation);
    }

    @Transactional
    public AppOperationCode saveOperationCode(AppOperationCode code) {
        return operationCodeDao.save(code);
    }

    @Transactional
    public void deleteOperationCode(Long id) {
        operationCodeDao.delete(id);
    }

    public Iterable<AppOperation> getAll() {
        return operationDao.findAll();
    }

    public Iterable<AppOperation> getByAppTypeId(long appTypeId) {
        return operationDao.findByAppTypeId(appTypeId);
    }
}
