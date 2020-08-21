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
    private String describes; // 参数描述

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

    @Column(length = 256, nullable = false)
    public String getDescribes() {
        return describes;
    }

    public void setDescribes(final String describes) {
        this.describes = describes;
    }
}
