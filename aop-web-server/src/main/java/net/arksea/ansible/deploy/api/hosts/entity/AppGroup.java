package net.arksea.ansible.deploy.api.hosts.entity;

import net.arksea.ansible.deploy.api.auth.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 应用分组
 * Create by xiaohaixing on 2020/8/20
 */
@Entity
@Table(name = "dp2_app_group")
public class AppGroup extends IdEntity {
    private String describes; // 应用分组描述
    private Set<App> apps;    // 分组管理的应用
    private Set<Host> hosts;  // 分组管理的主机
    private Set<User> users;  // 加入分组的用户

    @NotNull
    @Column(length = 64, nullable = false)
    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    @OneToMany(mappedBy = "appGroup", cascade = CascadeType.ALL)
    public Set<App> getApps() {
        return apps;
    }

    public void setApps(Set<App> apps) {
        this.apps = apps;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "dp2_app_group_hosts",
            joinColumns = @JoinColumn(name = "app_group_id"),
            inverseJoinColumns = @JoinColumn(name = "host_id"))
    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "dp2_app_group_users",
            joinColumns = @JoinColumn(name = "app_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
