package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Port;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface PortDao extends CrudRepository<Port, Long> {
    @Query(value="select count(1) from Port p where p.value>=?1 and p.value <=?2")
    long countByRange(int minValue, int maxValue);

    @Query(value="select count(1) from Port p where p.value>=?1 and p.value <=?2 and p.appId is not null")
    long countHasUsedByRange(int minValue, int maxValue);

    @Modifying
    @Query(value="delete from Port p where p.value>=?1 and p.value <=?2")
    int deleteByRange(int minValue, int maxValue);

    @Modifying
    @Query(nativeQuery = true,
           value="update dp2_port p set p.app_id=?1 where p.type_id=?2 and p.app_id is null and p.enabled is true limit 1")
    int setAppIdByTypeId(long appId, int typeId);

    List<Port> findByAppId(long appId);
}
