package net.arksea.ansible.deploy.api.manage.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@Entity
@Table(name = "dp2_app_operation")
public class AppOperation extends IdEntity {
    private AppType appType;
    private String name;
    private String description;
    private String command;
    private Set<AppOperationCode> codes;
    private Set<OperationVarDefine> varDefines;
    private Boolean released;
    private AppOperationType type;

    @ManyToOne
    @JoinColumn(name="app_type_id", nullable = false)
    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    @Column(length = 32, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 128, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 256, nullable = false)
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @OneToMany(mappedBy = "operationId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    public Set<AppOperationCode> getCodes() {
        return codes;
    }

    public void setCodes(Set<AppOperationCode> codes) {
        this.codes = codes;
    }

    @OneToMany(mappedBy = "operationId", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    public Set<OperationVarDefine> getVarDefines() {
        return varDefines;
    }

    public void setVarDefines(Set<OperationVarDefine> varDefines) {
        this.varDefines = varDefines;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    @Column(columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'COMMON'")
    @Enumerated(EnumType.STRING)
    public AppOperationType getType() {
        return type;
    }

    public void setType(AppOperationType type) {
        this.type = type;
    }
}
