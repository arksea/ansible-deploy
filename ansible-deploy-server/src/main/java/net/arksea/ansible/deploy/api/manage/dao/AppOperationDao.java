package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface AppOperationDao extends CrudRepository<AppOperation, Long> {
    Iterable<AppOperation> findByAppTypeId(long appTypeId);
}
