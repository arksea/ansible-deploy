package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.PortType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
interface PortTypeDao extends CrudRepository<PortType, Long> {
    @Modifying
    @Query(value="update PortType p set p.restCount=p.restCount+?1 where p.id=?2 and p.restCount+?1<=p.allCount and p.restCount+?1>=0")
    int incRestCount(int count, Long typeId);

    @Modifying
    @Query(value="update PortType p set p.restCount=p.restCount+?1, p.allCount=p.allCount+?2 where p.id=?3")
    int incSectionPorts(int restCount, int allCount, Long typeId);

    @Modifying
    @Query(value="update PortType p set p.restCount=p.restCount+?1, p.allCount=p.allCount+?1 where p.id=?2")
    int incUnusedPorts(int count, Long typeId);
}
