package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.App;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author xiaohaixing
 */
public interface AppDao extends  PagingAndSortingRepository<App, Long>{
   App findByApptag(String tag);
}
