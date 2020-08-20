package net.arksea.ansible.deploy.api.auth.info;

import java.io.Serializable;

/**
 * Create by xiaohaixing on 2020/8/19
 */
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 4354628788084070140L;
    public final Long userId;
    public final String username;
    public final String remoteAddress;

    public ClientInfo(Long userId, String username, String remoteAddress) {
        this.userId = userId;
        this.username = username;
        this.remoteAddress = remoteAddress;
    }
}
