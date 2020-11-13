package net.arksea.ansible.deploy.api.manage.msg;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * Create by xiaohaixing on 2020/11/13
 */
public class OperationJobInfo {
    private Long jobId;
    private String operation;
    private String operator;
    private Timestamp startTime;

    private OperationJobInfo() {
    }

    public OperationJobInfo(Long jobId, String operation, String operator, Timestamp startTime) {
        this.jobId = jobId;
        this.operation = operation;
        this.operator = operator;
        this.startTime = startTime;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperator() {
        return operator;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Timestamp getStartTime() {
        return startTime;
    }
}
