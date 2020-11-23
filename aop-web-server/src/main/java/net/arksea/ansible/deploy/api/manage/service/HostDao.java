package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.Host;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface HostDao extends CrudRepository<Host, Long>, JpaSpecificationExecutor<Host> {
    Iterable<Host> findByAppGroupId(Long appGroupId);
    @Query("select count(*) from Host h where h.appGroup.id = ?1")
    long countInAppGroup(long appgroupId);
}
