package net.arksea.ansible.deploy.api.operator.dao;

import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * Create by xiaohaixing on 2020/9/30
 */
public interface OperationJobDao extends CrudRepository<OperationJob, Long>, JpaSpecificationExecutor<OperationJob> {
    @Modifying
    @Query("delete OperationJob where startTime<?1")
    int deleteExpireJobs(Timestamp dateTime);
}
