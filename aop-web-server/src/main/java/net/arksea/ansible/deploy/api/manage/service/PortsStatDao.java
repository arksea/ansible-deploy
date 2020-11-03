package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.PortsStat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
interface PortsStatDao extends CrudRepository<PortsStat, Long> {
    PortsStat findByTypeId(Long typeId);

    @Modifying
    @Query(value="update PortsStat p set p.restCount=p.restCount+?1 where p.typeId=?2 and p.restCount+?1<=p.allCount and p.restCount+?1>=0")
    int incRestCount(int count, Long typeId);

    @Modifying
    @Query(value="update PortsStat p set p.restCount=p.restCount+?1, p.allCount=p.allCount+?1 where p.typeId=?2")
    int incAllCount(int count, Long typeId);
}
