package net.arksea.ansible.deploy.api.auth.dao;

import net.arksea.ansible.deploy.api.auth.entity.Permission;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;
import java.util.List;

@Resource(name="permissionDao")
public interface PermissionDao extends CrudRepository<Permission, Long> {
    List<Permission> findByPid(Long pid);
    List<Permission> findByPermission(String permission);
}
