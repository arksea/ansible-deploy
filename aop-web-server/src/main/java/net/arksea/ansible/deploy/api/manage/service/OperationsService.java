package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppOperationCodeDao;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationCode;
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
    @Autowired
    AppOperationCodeDao operationCodeDao;

    @Transactional
    public AppOperation saveOperation(AppOperation operation) {
        AppOperation saved = operationDao.save(operation);
        for (AppOperationCode c: operation.getCodes()) {
            c.setOperationId(saved.getId());
            operationCodeDao.save(c);
        }
        return saved;
    }

    @Transactional
    public AppOperationCode saveOperationCode(AppOperationCode code) {
        return operationCodeDao.save(code);
    }

    public Iterable<AppOperation> getAll() {
        return operationDao.findAll();
    }

    public Iterable<AppOperation> getByAppTypeId(long appTypeId) {
        return operationDao.findByAppTypeId(appTypeId);
    }
}
