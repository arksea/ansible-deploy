package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author xiaohaixing
 */
public interface OperationTriggerDao extends CrudRepository<OperationTrigger, Long> {
    OperationTrigger findByProjectTag(String projectTag);
    OperationTrigger findByProjectTagAndToken(String projectTag, String token);
}
