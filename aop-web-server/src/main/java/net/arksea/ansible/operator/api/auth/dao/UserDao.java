package net.arksea.ansible.operator.api.auth.dao;

import net.arksea.ansible.operator.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;
import java.util.List;

@Resource(name="userDao")
public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findOneByName(String name);
    List<User> findByName(String name);
    boolean existsByName(String name);
}
