package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.arksea.ansible.deploy.api.auth.entity.User;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Set;

/**
 * 应用分组
 * Create by xiaohaixing on 2020/8/20
 */
@Entity
@Table(name = "dp2_app_group")
public class AppGroup extends IdEntity implements Comparable<AppGroup> {
    private String name;       // 分组名称
    private String description;// 分组描述
    private String avatar;     // 分组头像
    private Set<User> users;   // 加入分组的用户
    private long appCount;      // 应用数
    private long userCount;     // 用户数
    private long hostCount;     // 主机数

    @NotBlank
    @Column(length = 64, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 128)
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Column(length = 128)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dp2_app_group_users",
            joinColumns = @JoinColumn(name = "app_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @OrderBy("name")
    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Transient
    public long getAppCount() {
        return appCount;
    }

    public void setAppCount(long appCount) {
        this.appCount = appCount;
    }

    @Transient
    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    @Transient
    public long getHostCount() {
        return hostCount;
    }

    public void setHostCount(long hostCount) {
        this.hostCount = hostCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AppGroup) {
            AppGroup g = (AppGroup)o;
            if (g.id == null && this.id == null) {
                return this.name.equals(g.name);
            } else if (g.id == null || this.id == null) {
                return false;
            }
            return this.id.equals(g.id);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(AppGroup o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return super.hashCode();
        } else {
            return name.hashCode();
        }
    }
}
