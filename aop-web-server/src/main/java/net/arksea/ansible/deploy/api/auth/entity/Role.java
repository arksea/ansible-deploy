package net.arksea.ansible.deploy.api.auth.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "sys_roles")
public class Role extends IdEntity {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String role;
    private String description;
    private boolean available;
    private Set<Permission> permissions;

    public Role() {
    }

    @NotBlank
    @Column(length = 24, nullable = false, unique = true)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column(length = 64)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="sys_roles_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "role="+role+",description="+description+",available="+available;
    }

    public String makeContent() {
        String content = "";
        String perInfo = "无";
        if (permissions != null && permissions.size() > 0) {
            perInfo = "";
            for (Permission p : permissions) {
                perInfo += ("【" + p.getId() + "】" + p.getPermission() + "；");
            }
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("角色名", role);
            map.put("描述", description);
            map.put("是否可用", available);
            map.put("分配权限", perInfo);
            content = objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            content = "{" +
                    "id:" + id +
                    ", 角色名:" + role +
                    ", 描述:" + description +
                    ", 是否可用:" + available +
                    ", 分配权限:" + perInfo +
                    "}";
        }
        return content;
    }
}