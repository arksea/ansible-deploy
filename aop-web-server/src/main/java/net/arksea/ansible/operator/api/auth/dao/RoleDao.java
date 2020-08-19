package net.arksea.ansible.operator.api.auth.dao;

import net.arksea.ansible.operator.api.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;
import java.util.List;

@Resource(name="roleDao")
public interface RoleDao extends CrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    List<Role> findByRole(String role);
}
