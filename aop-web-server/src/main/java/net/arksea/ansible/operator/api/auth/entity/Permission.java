package net.arksea.ansible.operator.api.auth.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "sys_permissions")
public class Permission extends IdEntity implements Comparable<Permission> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public String makeContent() {
        String content = "";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("权限", permission);
            map.put("描述", description);
            map.put("是否可用", available);
            map.put("归属节点", pid);
            content = objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            throw new RuntimeException("make json failed", ex);
        }
        return content;
    }
}