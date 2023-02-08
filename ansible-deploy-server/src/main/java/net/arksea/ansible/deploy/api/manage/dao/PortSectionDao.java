package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface PortSectionDao extends CrudRepository<PortSection, Long> {
    @Query(value = "select p from PortSection p order by p.minValue")
    List<PortSection> findAllOrderByMinValue();
    List<PortSection> findByTypeId(Long typeId);
}
