package net.arksea.ansible.deploy.api.manage.msg;

import net.arksea.ansible.deploy.api.manage.entity.App;

import java.io.Serializable;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/11/18
 */
public class GetUserApps {

    public static class Request implements Serializable {
        private static final long serialVersionUID = -358297229081734766L;
        public final long userId;
        public final String nameSearch;
        public final int page;
        public final int pageSize;

        public Request(long userId, String nameSearch, int page, int pageSize) {
            this.userId = userId;
            this.nameSearch = nameSearch;
            this.page = page;
            this.pageSize = pageSize;
        }
    }

    public static class Response implements Serializable {
        private static final long serialVersionUID = -5233251491835569542L;
        public final long total;
        public final long totalPages;
        public final List<App> items;

        public Response(long total, long totalPages, List<App> items) {
            this.total = total;
            this.totalPages = totalPages;
            this.items = items;
        }
    }
}
