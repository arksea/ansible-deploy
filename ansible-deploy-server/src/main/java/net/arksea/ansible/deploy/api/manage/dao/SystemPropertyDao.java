package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.SystemProperty;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author xiaohaixing
 */
public interface SystemPropertyDao extends CrudRepository<SystemProperty, Long> {
    SystemProperty findByName(String name);
}
