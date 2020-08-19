package net.arksea.ansible.operator.api.auth.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionInfo implements Serializable {
    private static final long serialVersionUID = 6312355516643145605L;

    public final long id;
    public final String permission;
    public final String description;
    public final int available;
    public final long pid;
    public final int level;

    public final List<PermissionInfo> childList;

    public PermissionInfo(long id, String permission, String description, int available, long pid, int level) {
        this.id = id;
        this.permission = permission;
        this.description = description;
        this.available = available;
        this.pid = pid;
        this.level = level;
        this.childList = new ArrayList<>();
    }
}
