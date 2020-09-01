package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.App;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 * @author xiaohaixing
 */
public interface AppDao extends  PagingAndSortingRepository<App, Long>{
   App findByApptag(String tag);
   @Query(nativeQuery = true,
          value ="select app.* from dp2_app_group_users gu, dp2_app app " +
                 " where gu.user_id = ? and app.app_group_id = gu.app_group_id and app.deleted = false")
   List<App> findByUserId(long userId);

   @Modifying
   @Query("update App a set a.deleted=?2 where a.id =?1")
   void updateDeletedById(long id, boolean deleted);
}
