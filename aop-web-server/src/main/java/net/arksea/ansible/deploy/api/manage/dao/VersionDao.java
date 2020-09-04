package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Version;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author xiaohaixing
 */
public interface VersionDao extends CrudRepository<Version, Long> {
}
