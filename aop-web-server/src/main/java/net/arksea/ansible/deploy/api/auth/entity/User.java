package net.arksea.ansible.deploy.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "sys_users", indexes = {@Index(columnList = "lastLogin")})
public class User extends IdEntity {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String name;
    private String email;
    private String plainPassword;
    private String password;
    private String salt;
    private Date registerDate;
    private Date lastLogin;
    private boolean locked;
    private Set<Role> roles;

    public User() {
    }

    public User(long id) {
        setId(id);
    }

    @NotBlank
    @Column(length = 24, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Column(length = 32, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 不持久化到数据库，也不参与Json序列化.
    @Transient
    @JsonIgnore
    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    @Column(length = 44)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(length = 44, nullable = false)
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @Column(nullable = false)
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @Column(nullable = false)
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Column(nullable = false)
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "name=" + name + ",email=" + email + ",locked=" + locked;
    }

    public String makeContent() {
        String content = "";
        String roleInfo = "无";
        if (roles != null && roles.size() > 0) {
            roleInfo = "";
            for (Role r : roles) {
                roleInfo += ("【" + r.getId() + "】" + r.getRole() + "；");
            }
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("账号", name);
            map.put("邮箱", email);
            map.put("是否锁定", locked);
            map.put("分配角色", roleInfo);
            content = objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            throw new RuntimeException("make json failed", ex);
        }
        return content;
    }
}
