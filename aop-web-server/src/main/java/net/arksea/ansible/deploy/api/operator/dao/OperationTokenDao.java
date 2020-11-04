package net.arksea.ansible.deploy.api.operator.dao;

import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/30
 */
public interface OperationTokenDao extends CrudRepository<OperationToken, Long> {
    @Modifying
    @Query(nativeQuery = true,
           value = "update dp2_operation_token t set t.released=false, t.holder_id=?2, t.job_id=?3, t.hold_time=now()" +
                   " where t.app_id=?1 and (t.released=true or UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(t.hold_time)>?4)")
    int hold(long appId, long userId, long jobId, long timeoutSeconds);

    OperationToken findByAppId(long appId);

    void deleteByAppId(long appId);
}
