package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Port;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface PortDao extends CrudRepository<Port, Long> {
    @Query(value="select count(1) from Port p where p.value>=?1 and p.value <=?2")
    long countByRange(int minValue, int maxValue);
}
