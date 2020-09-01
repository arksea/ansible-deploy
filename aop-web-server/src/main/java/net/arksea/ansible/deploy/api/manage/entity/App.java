package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class App extends IdEntity {
    private String apptag;     //应用标签，通常用来部署时建立应用目录名
    private String apptype;    //应用的类型
    private String deployPath; //应用部署目标路径
    private String description;  //应用描述
    private AppGroup appGroup;
    private Set<GroupVar> vars;// 变量
    private Set<Port> ports;
    private boolean enableJmx;
    private Set<Version> versions;
    private Timestamp createTime; //创建时间
    private boolean deleted; //是否标记为删除状态，系统将定时删除标记为删除状态的记录

    @NotBlank
    @Column(length = 20, nullable = false, unique = true)
    public String getApptag() {
        return apptag;
    }

    public void setApptag(final String apptag) {
        this.apptag = apptag;
    }

    @NotBlank
    @Column(length = 20, nullable = false)
    public String getApptype() {
        return apptype;
    }

    public void setApptype(final String apptype) {
        this.apptype = apptype;
    }

    @Column(nullable = false, columnDefinition = "varchar(128) DEFAULT ''")
    public String getDeployPath() {
        return deployPath;
    }

    public void setDeployPath(final String deployPath) {
        this.deployPath = deployPath;
    }

    @NotNull
    @Column(length = 255, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "app_group_id", nullable = false)
    @JsonBackReference
    public AppGroup getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(AppGroup appGroup) {
        this.appGroup = appGroup;
    }

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE) //在数据库层面进行级联删除操作（生成库表时定义的外键会加 ON DELETE CASCADE修饰词）
    public Set<GroupVar> getVars() {
        return vars;
    }

    public void setVars(final Set<GroupVar> vars) {
        this.vars = vars;
    }

    @OneToMany(mappedBy = "app", fetch = FetchType.EAGER)
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

    @Column(nullable = false, columnDefinition = "TINYINT NOT NULL DEFAULT 1")
    public boolean isEnableJmx() {
        return enableJmx;
    }

    public void setEnableJmx(final boolean enableJmx) {
        this.enableJmx = enableJmx;
    }

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    @Column(nullable = false, columnDefinition = "TINYINT NOT NULL DEFAULT 1")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
