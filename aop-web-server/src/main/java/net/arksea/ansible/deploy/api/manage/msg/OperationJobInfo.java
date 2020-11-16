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
    private String version;
    private Timestamp startTime;
    private Timestamp endTime;

    private OperationJobInfo() {
    }

    public OperationJobInfo(Long jobId, String operation, String operator, String ver, Timestamp startTime, Timestamp endTime) {
        this.jobId = jobId;
        this.operation = operation;
        this.operator = operator;
        this.version = ver;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getVersion() {
        return version;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Timestamp getStartTime() {
        return startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Timestamp getEndTime() {
        return endTime;
    }
}
