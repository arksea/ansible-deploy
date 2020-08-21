package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Host;
import org.springframework.data.repository.CrudRepository;

public interface ManagedHostDao extends CrudRepository<Host, Long> {
}
