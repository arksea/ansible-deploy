package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppVariable;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author xiaohaixing
 */
public interface AppVarDao extends CrudRepository<AppVariable, Long> {
    AppVariable findByAppIdAndName(long appId, String name);
}
