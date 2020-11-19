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
        public final Long groupId;
        public final String ipSearch;
        public final int page;
        public final int pageSize;

        /**
         *
         * @param groupId  null: 表示未分组，<0表示：所有主机，>0：指定分组
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
