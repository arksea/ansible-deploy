package net.arksea.ansible.deploy.api.manage.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "dp2_app_version", uniqueConstraints = @UniqueConstraint(columnNames = { "appId", "name" }))
public class Version extends IdEntity {
    private String name;

    private String repository;

    private String execOpt;

    private String revision;

    private Set<Host> targetHosts;

    private long appId;

    private Set<VersionVariable> vars;// 变量

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
    public long getAppId() {
        return appId;
    }

    public void setAppId(final long appId) {
        this.appId = appId;
    }

    @ManyToMany(fetch = FetchType.EAGER)
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
    @OnDelete(action = OnDeleteAction.CASCADE) //在数据库层面进行级联删除操作（生成库表时定义的外键会加 ON DELETE CASCADE修饰词）
    @OrderBy("id")
    public Set<VersionVariable> getVars() {
        return vars;
    }

    public void setVars(final Set<VersionVariable> vars) {
        this.vars = vars;
    }
}
