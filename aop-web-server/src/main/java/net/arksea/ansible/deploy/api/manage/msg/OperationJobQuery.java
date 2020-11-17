package net.arksea.ansible.deploy.api.manage.msg;

/**
 * 操作历史分页查询条件
 * Create by xiaohaixing on 2020/11/17
 */
public class OperationJobQuery {
    public final long appId;
    public final int page;
    public final int pageSize;
    public final String startTime;
    public final String endTime;
    public final String operator;

    public OperationJobQuery(long appId, int page, int pageSize, String startTime, String endTime, String operator) {
        this.appId = appId;
        this.page = page;
        this.pageSize = pageSize;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operator = operator;
    }
}
