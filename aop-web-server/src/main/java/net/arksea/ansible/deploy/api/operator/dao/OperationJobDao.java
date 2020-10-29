package net.arksea.ansible.deploy.api.operator.dao;

import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/30
 */
public interface OperationJobDao extends CrudRepository<OperationJob, Long> {
}
