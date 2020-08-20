package net.arksea.ansible.deploy.api.auth.info;

import java.io.Serializable;
import java.util.List;

public class GetRoleList {
    public static class Response implements Serializable {

        private static final long serialVersionUID = 1070793757876972898L;

        public final long total;
        public final int totalPages;
        public final List<RoleInfo> roleList;

        public Response(long total, int totalPages, List<RoleInfo> roleList) {
            this.total = total;
            this.totalPages = totalPages;
            this.roleList = roleList;
        }
    }
}
