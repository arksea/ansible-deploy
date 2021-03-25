package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppVariable;
import net.arksea.ansible.deploy.api.manage.entity.VersionVariable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 * @author xiaohaixing
 */
public interface VersionVarDao extends CrudRepository<VersionVariable, Long> {
    VersionVariable findByVersionIdAndName(long versionId, String name);
}
