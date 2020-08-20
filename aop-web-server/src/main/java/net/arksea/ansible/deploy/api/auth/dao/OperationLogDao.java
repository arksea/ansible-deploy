package net.arksea.ansible.deploy.api.auth.dao;

import net.arksea.ansible.deploy.api.auth.entity.OperationLog;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;

@Resource(name = "operationLogDao")
public interface OperationLogDao extends CrudRepository<OperationLog, Long> {
}
