package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Version;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author xiaohaixing
 */
public interface VersionDao extends CrudRepository<Version, Long> {
    Version findByAppIdAndName(long appId, String name);
    @Modifying
    @Query(nativeQuery = true, value="insert into dp2_version_hosts set version_id=?1, host_id=?2")
    void addHost(long versionId, long hostId);

    @Modifying
    @Query(nativeQuery = true, value="delete from dp2_version_hosts where version_id=?1 and host_id=?2")
    void removeHost(long versionId, long hostId);

    @Modifying
    @Query("update Version v set v.buildNo = ?2 where v.id = ?1")
    int updateBuildNo(long versionId, long buildNo);
}
