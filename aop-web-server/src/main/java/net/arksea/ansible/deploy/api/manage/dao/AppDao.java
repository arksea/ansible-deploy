package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.App;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 * @author xiaohaixing
 */
public interface AppDao extends CrudRepository<App, Long> {
   App findByApptag(String tag);

   @Query(nativeQuery = true,
           value ="select app.* from dp2_app_group_users gu, dp2_app app " +
                   " where gu.user_id = ?1 and app.app_group_id = gu.app_group_id limit ?2, ?3")
   List<App> findPageByUserId(long userId, int offset, int count);

   @Query(nativeQuery = true,
           value ="select app.* from dp2_app_group_users gu, dp2_app app " +
                   " where gu.user_id = ?1 and app.app_group_id = gu.app_group_id and app.apptag like ?2 limit ?3, ?4")
   List<App> findPageByUserId(long userId, String nameSearch, int offset, int count);

   @Query(nativeQuery = true,
           value ="select count(*) from dp2_app_group_users gu, dp2_app app " +
                   " where gu.user_id = ?1 and app.app_group_id = gu.app_group_id")
   long getUserAppsCount(long userId);

   @Query(nativeQuery = true,
           value ="select count(*) from dp2_app_group_users gu, dp2_app app " +
                   " where gu.user_id = ?1 and app.app_group_id = gu.app_group_id and app.apptag like ?2")
   long getUserAppsCount(long userId, String nameSearch);

   @Query("select a from App a where a.appGroupId is null")
   List<App> findAllGroupIsNull();
}
