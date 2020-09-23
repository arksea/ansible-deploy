package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.PortsStat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface PortsStatDao extends CrudRepository<PortsStat, Integer> {
    PortsStat findByTypeId(int typeId);

    @Modifying
    @Query(value="update PortsStat p set p.restCount=p.restCount+?1 where p.typeId=?2 and p.restCount+?1<=p.allCount and p.restCount+?1>=0")
    int incRestCount(int count, int typeId);

    @Modifying
    @Query(value="update PortsStat p set p.restCount=p.restCount+?1, p.allCount=p.allCount+?1 where p.typeId=?2")
    int incAllCount(int count, int typeId);
}
