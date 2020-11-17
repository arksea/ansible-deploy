package net.arksea.ansible.deploy.api.manage.msg;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/11/17
 */
public class OperationJobPage {
    public final Long total;
    public final int totalPages;
    public final List<OperationJobInfo> items;

    public OperationJobPage(Long total, int totalPages, List<OperationJobInfo> items) {
        this.total = total;
        this.totalPages = totalPages;
        this.items = items;
    }
}
