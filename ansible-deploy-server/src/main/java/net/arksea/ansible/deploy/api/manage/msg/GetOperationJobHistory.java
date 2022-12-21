package net.arksea.ansible.deploy.api.manage.msg;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/11/18
 */
public class GetOperationJobHistory {

    public static class OperationJobInfo implements Serializable {
        private static final long serialVersionUID = -4429885376298050312L;
        public final Long jobId;
        public final String operation;
        public final String operator;
        public final Long triggerId;
        public final String version;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
        public final Timestamp startTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
        public final Timestamp endTime;

        public OperationJobInfo(Long jobId, String operation, String operator, Long triggerId, String ver, Timestamp startTime, Timestamp endTime) {
            this.jobId = jobId;
            this.operation = operation;
            this.operator = operator;
            this.triggerId = triggerId;
            this.version = ver;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public static class Request implements Serializable {
        private static final long serialVersionUID = -2466367661485734524L;
        public final long appId;
        public final int page;
        public final int pageSize;
        public final String startTime;
        public final String endTime;
        public final String operator;

        public Request(long appId, int page, int pageSize, String startTime, String endTime, String operator) {
            this.appId = appId;
            this.page = page;
            this.pageSize = pageSize;
            this.startTime = startTime;
            this.endTime = endTime;
            this.operator = operator;
        }
    }

    public static class Response implements Serializable {
        private static final long serialVersionUID = 6973666409552763009L;
        public final Long total;
        public final int totalPages;
        public final List<OperationJobInfo> items;

        public Response(Long total, int totalPages, List<OperationJobInfo> items) {
            this.total = total;
            this.totalPages = totalPages;
            this.items = items;
        }
    }
}
