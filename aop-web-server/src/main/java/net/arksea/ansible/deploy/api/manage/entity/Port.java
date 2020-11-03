package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

@Entity
@Table(name = "dp2_port")
public class Port extends IdEntity {
    private Long typeId;
    private int value;
    private boolean enabled;
    private Long appId;

    @Column(nullable = false)
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(nullable = false, unique = true, columnDefinition = "INT UNSIGNED")
    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Column
    public Long getAppId() {
        return appId;
    }

    public void setAppId(final Long appId) {
        this.appId = appId;
    }
}
