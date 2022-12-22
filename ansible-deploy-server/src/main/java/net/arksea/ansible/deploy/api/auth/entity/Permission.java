package net.arksea.ansible.deploy.api.auth.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "sys_permissions")
public class Permission extends IdEntity implements Comparable<Permission> {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String permission;  //权限
    private String description; //描述
    private Long pid;           //父权限ID
    private boolean available;  //是否生效

    public Permission() {
    }

    @NotBlank
    @Column(length = 32, nullable = false, unique = true)
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Column(length = 64)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column
    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    @Column(nullable = false)
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "permission="+permission+",description="+description+",available="+available;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Permission) {
            if (o == this) {
                return true;
            } else {
                return this.permission.equals(((Permission) o).permission);
            }
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Permission o) {
        return this.permission.compareTo(o.permission);
    }

    @Override
    public int hashCode() {
        return this.permission.hashCode();
    }
}