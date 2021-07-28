package net.arksea.ansible.deploy.api.manage.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "dp2_app_version", uniqueConstraints = @UniqueConstraint(columnNames = { "appId", "name" }))
public class Version extends IdEntity {
    private String name;

    private String repository; //安装包仓库（路径）

    private String execOpt; //版本运行参数

    private String revision; //安装包仓库Revision

    private long buildNo; //最近的构建号

    private Timestamp buildNoUpdate; //构建号更新时间

    private Long deployNo; //最近用于部署的构建号

    private Timestamp deployNoUpdate; //部署时间

    private Set<Host> targetHosts; //部署操作主机

    private long appId;

    private Set<VersionVariable> vars;// 版本变量

    private Set<OperationTrigger> triggers;

    @NotBlank
    @Column(name = "repo_path", length = 1024, nullable = false)
    public String getRepository() {
        return repository;
    }

    public void setRepository(final String repository) {
        this.repository = repository;
    }

    @Column(name = "exec_opt", length = 1024)
    public String getExecOpt() {
        return execOpt;
    }

    public void setExecOpt(final String execOpt) {
        this.execOpt = execOpt;
    }

    @NotBlank
    @Column(nullable = false, columnDefinition = "varchar(64) NOT NULL DEFAULT 'HEAD'")
    public String getRevision() {
        return revision;
    }

    public void setRevision(final String revision) {
        this.revision = revision;
    }

    @Column(nullable = false)
    public long getBuildNo() {
        return buildNo;
    }

    public void setBuildNo(long buildNo) {
        this.buildNo = buildNo;
    }

    @Column
    public Timestamp getBuildNoUpdate() {
        return buildNoUpdate;
    }

    public void setBuildNoUpdate(Timestamp buildNoUpdate) {
        this.buildNoUpdate = buildNoUpdate;
    }

    @Column
    public Long getDeployNo() {
        return deployNo;
    }

    public void setDeployNo(Long deployNo) {
        this.deployNo = deployNo;
    }

    @Column
    public Timestamp getDeployNoUpdate() {
        return deployNoUpdate;
    }

    public void setDeployNoUpdate(Timestamp deployNoUpdate) {
        this.deployNoUpdate = deployNoUpdate;
    }

    @Column(nullable = false)
    public long getAppId() {
        return appId;
    }

    public void setAppId(final long appId) {
        this.appId = appId;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(name = "dp2_version_hosts",
            joinColumns = @JoinColumn(name = "version_id"),
            inverseJoinColumns = @JoinColumn(name = "host_id"))
    public Set<Host> getTargetHosts() {
        return targetHosts;
    }

    public void setTargetHosts(final Set<Host> targetHosts) {
        this.targetHosts = targetHosts;
    }

    @NotBlank
    @Column(nullable = false, length = 64)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "versionId", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.CASCADE) //在数据库层面进行级联删除操作（生成库表时定义的外键会加 ON DELETE CASCADE修饰词）
    @OrderBy("id")
    public Set<VersionVariable> getVars() {
        return vars;
    }

    public void setVars(final Set<VersionVariable> vars) {
        this.vars = vars;
    }

    @OneToMany(mappedBy = "versionId", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.CASCADE) //在数据库层面进行级联删除操作（生成库表时定义的外键会加 ON DELETE CASCADE修饰词）
    @OrderBy("id")
    public Set<OperationTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<OperationTrigger> triggers) {
        this.triggers = triggers;
    }
}
