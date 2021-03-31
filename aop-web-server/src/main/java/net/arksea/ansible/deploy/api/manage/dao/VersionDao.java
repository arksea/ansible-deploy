package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.Version;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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
    @Query("update Version v set v.buildNo = ?2, v.buildNoUpdate = now() where v.id = ?1")
    int updateBuildNo(long versionId, long buildNo);

    @Modifying
    @Query("update Version v set v.deployNo = ?2, v.deployNoUpdate = now() where v.id = ?1")
    int updateDeployNo(long versionId, long buildNo);

    @Query(nativeQuery = true, value="select v.* from dp2_app a " +
           " inner join dp2_app_version v on a.id = v.app_id and a.app_type_id = ?1")
    List<Version> findByAppType(long appTypeId);

}
