package net.arksea.ansible.deploy.api.manage.msg;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * Create by xiaohaixing on 2020/11/13
 */
public class OperationJobInfo {
    public final Long jobId;
    public final String operation;
    public final String operator;
    public final String version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public final Timestamp startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public final Timestamp endTime;

    public OperationJobInfo(Long jobId, String operation, String operator, String ver, Timestamp startTime, Timestamp endTime) {
        this.jobId = jobId;
        this.operation = operation;
        this.operator = operator;
        this.version = ver;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
