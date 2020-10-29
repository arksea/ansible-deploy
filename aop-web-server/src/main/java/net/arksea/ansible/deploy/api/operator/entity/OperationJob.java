package net.arksea.ansible.deploy.api.operator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by xiaohaixing on 2020/9/30
 */
@Entity
@Table(name = "dp2_operation_job")
public class OperationJob extends IdEntity {
    private Long appId;
    private Long operatorId;
    private Long operationId;
    private String execHost;
    private Timestamp startTime;
    private Timestamp endTime;
    private String log;

    @Column(nullable = false)
    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Column(nullable = false)
    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Column(nullable = false)
    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Column(nullable = false, length = 36)
    public String getExecHost() {
        return execHost;
    }

    public void setExecHost(String execHost) {
        this.execHost = execHost;
    }

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Basic(fetch= FetchType.LAZY)
    @Lob
    @Column
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
