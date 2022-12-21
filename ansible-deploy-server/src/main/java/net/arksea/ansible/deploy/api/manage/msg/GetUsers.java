package net.arksea.ansible.deploy.api.manage.msg;

import net.arksea.ansible.deploy.api.auth.entity.User;

import java.io.Serializable;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/11/18
 */
public class GetUsers {

    public static class Request implements Serializable {
        private static final long serialVersionUID = -5291578996034121565L;
        public final String nameSearch;
        public final int page;
        public final int pageSize;

        /**
         * @param nameSearch
         * @param page
         * @param pageSize
         */
        public Request(String nameSearch, int page, int pageSize) {
            this.nameSearch = nameSearch;
            this.page = page;
            this.pageSize = pageSize;
        }
    }

    public static class Response implements Serializable {
        private static final long serialVersionUID = 832756237938550203L;
        public final long total;
        public final long totalPages;
        public final List<User> items;

        public Response(long total, long totalPages, List<User> items) {
            this.total = total;
            this.totalPages = totalPages;
            this.items = items;
        }
    }
}
