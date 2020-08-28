package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name = "dp2_group_vars", uniqueConstraints = @UniqueConstraint(columnNames = { "app_id", "name" }) )
public class GroupVar extends IdEntity {

    private App app; // 所属应用
    private String name;// 变量名
    private String value;// 变量值
    private String inputAddon;// 输入显示的前缀
    private String description; // 参数描述
    private Boolean isPort;// 是否端口值，用于主机范围的唯一性判断
    private String inputType;

    @ManyToOne
    @JoinColumn(name = "app_id", nullable = false)
    @JsonIgnore
    public App getApp() {
        return app;
    }

    public void setApp(final App app) {
        this.app = app;
    }

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Column(length = 256, nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Column(length = 256, nullable = true)
    public String getInputAddon() {
        return inputAddon;
    }

    public void setInputAddon(final String inputAddon) {
        this.inputAddon = inputAddon;
    }

    @Column(length = 256, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public Boolean getIsPort() {
        return isPort;
    }

    public void setIsPort(final Boolean isPort) {
        this.isPort = isPort;
    }

    @Transient
    public String getInputType() {
        return inputType;
    }

    public void setInputType(final String inputType) {
        this.inputType = inputType;
    }

}
