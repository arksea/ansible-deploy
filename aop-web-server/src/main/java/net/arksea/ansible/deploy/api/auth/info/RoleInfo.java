package net.arksea.ansible.deploy.api.auth.info;

import net.arksea.ansible.deploy.api.auth.entity.Permission;

import java.io.Serializable;
import java.util.Set;

public class RoleInfo implements Serializable {
    private static final long serialVersionUID = 1440325604535389950L;

    public final long id;
    public final int available;
    public final String description;
    public final String role;
    public final Set<Permission> permissions;

    public RoleInfo(long id, int available, String description, String role, Set<Permission> permissions) {
        this.id = id;
        this.available = available;
        this.description = description;
        this.role = role;
        this.permissions = permissions;
    }
}
