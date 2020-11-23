package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AppGroupDao extends CrudRepository<AppGroup, Long> {

    @Modifying
    @Query("update AppGroup g set g.name = ?2, g.description = ?3 where g.id = ?1")
    int updateInfo(long groupId, String name, String desc);

    @Modifying
    @Query(nativeQuery = true, value="insert into dp2_app_group_users set app_group_id=?1, user_id=?2")
    void addUserToGroup(long groupId, long userId);

    @Modifying
    @Query(nativeQuery = true, value="delete from dp2_app_group_users where app_group_id=?1 and user_id=?2")
    void removeUserFromGroup(long groupId, long userId);

    @Query(nativeQuery = true, value="select count(1) from dp2_app_group_users r where r.app_group_id=?1 and r.user_id=?2")
    long userInGroup(long groupId, long userId);

    @Query(nativeQuery = true, value="select g.* from dp2_app_group g,dp2_app_group_users r where r.user_id=?1 and r.app_group_id = g.id")
    Set<AppGroup> findByUserId(long userId);
}
