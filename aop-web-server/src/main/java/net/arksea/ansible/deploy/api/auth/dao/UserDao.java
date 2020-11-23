package net.arksea.ansible.deploy.api.auth.dao;

import net.arksea.ansible.deploy.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import javax.annotation.Resource;

@Resource(name="userDao")
public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findOneByName(String name);

    boolean existsByName(String name);

    @Query(nativeQuery = true,
           value ="select count(*) from sys_users u inner join dp2_app_group_users gu " +
                  " on gu.app_group_id = ?1 and u.id = gu.app_group_id")
    long countInAppGroup(long groupId);

    @Query(nativeQuery = true,
           value="select * from sys_users u where u.id not in (select g.user_id from dp2_app_group_users g where g.app_group_id=?1)")
    Iterable<User> findUsersNotInGroup(long groupId);

    @Modifying
    @Query("update User u set u.locked = true where u.id = ?1")
    void lockById(long id);

    @Modifying
    @Query("update User u set u.locked = false where u.id = ?1")
    void unlockById(long id);
}
