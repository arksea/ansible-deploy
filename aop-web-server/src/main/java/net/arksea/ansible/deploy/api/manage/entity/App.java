package net.arksea.ansible.deploy.api.manage.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Set;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name = "dp2_app")
public class App extends IdEntity implements Comparable<App> {
    private String apptag = "";     //应用标签，通常用来部署时建立应用目录名
    private AppType appType;
    private String description = "";  //应用描述
    private AppGroup appGroup;
    private Set<AppVariable> vars;// 变量
    private Set<Port> ports;
    private Set<Version> versions;
    private Timestamp createTime; //创建时间

    @NotBlank
    @Column(length = 32, nullable = false, unique = true)
    public String getApptag() {
        return apptag;
    }

    public void setApptag(final String apptag) {
        this.apptag = apptag;
    }

    @ManyToOne
    @JoinColumn(name="app_type_id",nullable = false)
    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    @NotNull
    @Column(length = 128, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "app_group_id")
    public AppGroup getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(AppGroup appGroup) {
        this.appGroup = appGroup;
    }

    @OneToMany(mappedBy = "appId", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.CASCADE) //在数据库层面进行级联删除操作（生成库表时定义的外键会加 ON DELETE CASCADE修饰词）
    @OrderBy("id")
    public Set<AppVariable> getVars() {
        return vars;
    }

    public void setVars(final Set<AppVariable> vars) {
        this.vars = vars;
    }

    @OneToMany(mappedBy = "appId", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @OrderBy("value")
    public Set<Port> getPorts() {
        return ports;
    }

    public void setPorts(final Set<Port> ports) {
        this.ports = ports;
    }

    @Override
    public String toString() {
        return apptag;
    }

    @OneToMany(mappedBy = "appId", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @OrderBy("id")
    public Set<Version> getVersions() {
        return versions;
    }

    public void setVersions(final Set<Version> versions) {
        this.versions = versions;
    }

    @Column(nullable = false, columnDefinition = ("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"))
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public int compareTo(App o) {
        return this.apptag.compareTo(o.apptag);
    }
}
