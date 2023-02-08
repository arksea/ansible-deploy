package net.arksea.ansible.deploy.api.manage.msg;

import net.arksea.ansible.deploy.api.manage.entity.Host;

import java.io.Serializable;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/11/18
 */
public class GetHosts {

    public static class Request implements Serializable {
        private static final long serialVersionUID = -2282597973878000475L;
        public final Long groupId;   //不为null表示查询某分组，null表示不设置过滤条件，相当于查询所有主机
        public final String ipSearch;
        public final int page;
        public final int pageSize;

        /**
         * @param groupId
         * @param ipSearch
         * @param page
         * @param pageSize
         */
        public Request(Long groupId, String ipSearch, int page, int pageSize) {
            this.groupId = groupId;
            this.ipSearch = ipSearch;
            this.page = page;
            this.pageSize = pageSize;
        }
    }

    public static class Response implements Serializable {
        private static final long serialVersionUID = 1990254640295310285L;
        public final long total;
        public final long totalPages;
        public final List<Host> items;

        public Response(long total, long totalPages, List<Host> items) {
            this.total = total;
            this.totalPages = totalPages;
            this.items = items;
        }
    }
}
