package net.arksea.ansible.deploy.api.auth.dao;

import net.arksea.ansible.deploy.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;
import java.util.List;

@Resource(name="userDao")
public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findOneByName(String name);
    List<User> findByName(String name);
    boolean existsByName(String name);
    Iterable<User> findAllByLocked(boolean locked);

    @Modifying
    @Query("update User u set u.locked = true where u.id = ?1")
    void lockById(long id);

    @Modifying
    @Query("update User u set u.locked = false where u.id = ?1")
    void unlockById(long id);
}
