package net.arksea.ansible.operator.api.auth.info;

import java.io.Serializable;
import java.util.List;

public class GetUserList {
    public static class Response implements Serializable {

        private static final long serialVersionUID = 6915404924555210180L;

        public final long total;
        public final int totalPages;
        public final List<UserInfo> userList;

        public Response(long total, int totalPages, List<UserInfo> userList) {
            this.total = total;
            this.totalPages = totalPages;
            this.userList = userList;
        }
    }
}
