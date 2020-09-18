package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.PortsStat;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface PortsStatDao extends CrudRepository<PortsStat, Integer> {
    PortsStat findByTypeId(int typeId);
}
