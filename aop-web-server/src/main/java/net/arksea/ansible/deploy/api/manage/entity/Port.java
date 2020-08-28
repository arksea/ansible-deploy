package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "dp2_port")
public class Port extends IdEntity {

    /** type */
    private int type;
    /** name */
    private String name;
    /** value */
    private int value;
    /** enable */
    private int enable;
    /** app */
    private App app;

    @Column(name = "type", nullable = false)
    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    @Column(name = "name", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Column(name = "value", nullable = false)
    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Column(name = "enable", nullable = false)
    public int getEnable() {
        return enable;
    }

    public void setEnable(final int enable) {
        this.enable = enable;
    }

    @ManyToOne
    @JoinColumn(name = "app_id", nullable = false)
    @JsonIgnore
    public App getApp() {
        return app;
    }

    public void setApp(final App app) {
        this.app = app;
    }

}
