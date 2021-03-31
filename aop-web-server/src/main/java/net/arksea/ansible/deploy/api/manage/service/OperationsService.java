package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppOperationCodeDao;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@Component
public class OperationsService {
    private static Logger logger = LogManager.getLogger(OperationsService.class);
    @Autowired
    AppOperationDao operationDao;
    @Autowired
    AppOperationCodeDao operationCodeDao;

    @Transactional
    public AppOperation saveOperation(AppOperation operation) {
        if (operation.getId() == null) {
            //新建操作因为涉及关联的code表，需要先保存operation对象以获得id，之后再进行级联保存codes
            Set<OperationVarDefine> defs = operation.getVarDefines();
            operation.setVarDefines(null);
            Set<AppOperationCode> codes = operation.getCodes();
            operation.setCodes(null);
            AppOperation saved = operationDao.save(operation);
            if (codes != null && codes.size() > 0) {
                 //新建操作因为涉及关联的code表，需要先保存operation对象以获得id，之后再进行级联保存codes
                 for (AppOperationCode c : codes) {
                     c.setOperationId(saved.getId());
                 }
                 saved.setCodes(codes);
            }
            if (defs != null && defs.size() > 0) {
                 for (OperationVarDefine d: defs) {
                     d.setOperationId(saved.getId());
                 }
                 saved.setVarDefines(defs);
            }
            return operationDao.save(saved);
        } else {
            AppOperation saved = operationDao.save(operation);
            Set<OperationVarDefine> newVars = saved.getVarDefines();
            logger.debug("newVars={}", newVars.stream()
                    .map(OperationVarDefine::getName)
                    .reduce("",(s,v) -> s + v + ";"));
            return saved;
        }
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
