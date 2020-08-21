package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import org.springframework.data.repository.CrudRepository;

public interface AppGroupDao extends CrudRepository<AppGroup, Long> {
}
