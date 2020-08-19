package net.arksea.ansible.operator.api.auth.dao;

import net.arksea.ansible.operator.api.auth.entity.OperationLog;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;

@Resource(name = "operationLogDao")
public interface OperationLogDao extends CrudRepository<OperationLog, Long> {
}
