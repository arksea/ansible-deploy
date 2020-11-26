package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.Host;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface HostDao extends CrudRepository<Host, Long>, JpaSpecificationExecutor<Host> {
    Iterable<Host> findByAppGroupId(Long appGroupId);

    boolean existsByPrivateIp(String privateIp);

    @Query(nativeQuery = true,
            value = "select h.* from dp2_hosts h order by inet_aton(h.private_ip) limit ?1, ?2")
    List<Host> findPage(int offset, int count);

    @Query(nativeQuery = true,
            value = "select h.* from dp2_hosts h "
                    + " where h.private_ip like ?3"
                    + " order by inet_aton(h.private_ip) limit ?1, ?2")
    List<Host> findPage(int offset, int count, String ipSearch);
    @Query(nativeQuery = true, value = "select count(*) from dp2_hosts h where h.private_ip like ?1")
    long count(String ipSearch);

    @Query(nativeQuery = true,
            value = "select h.* from dp2_hosts h "
                    + " where h.app_group_id = ?3"
                    + " order by inet_aton(h.private_ip) limit ?1, ?2")
    List<Host> findPage(int offset, int count, long groupId);
    @Query("select count(*) from Host h where h.appGroup.id = ?1")
    long count(long appGroupId);

    @Query(nativeQuery = true,
            value = "select h.* from dp2_hosts h "
                  + " where h.app_group_id = ?3 and h.private_ip like ?4"
                  + " order by inet_aton(h.private_ip) limit ?1, ?2")
    List<Host> findPage(int offset, int count, long groupId, String ipSearch);
    @Query(nativeQuery = true,
            value = "select count(*) from dp2_hosts h where h.app_group_id = ?1 and h.private_ip like ?2")
    long count(long groupId, String ipSearch);
}
