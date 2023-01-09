package net.arksea.ansible.deploy.api.auth.dao;

import net.arksea.ansible.deploy.api.auth.entity.Permission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PermissionDao extends CrudRepository<Permission, Long> {
    List<Permission> findByPid(Long pid);
    List<Permission> findByPermission(String permission);
}
