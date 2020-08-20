package net.arksea.ansible.deploy.api.hosts.dao;

import net.arksea.ansible.deploy.api.hosts.entity.Host;
import org.springframework.data.repository.CrudRepository;

public interface ManagedHostDao extends CrudRepository<Host, Long> {
}
