package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

@Entity
@Table(name = "dp2_port")
public class Port extends IdEntity {

    private long sectionId;
    private int typeId;
    private int value;
    private boolean enabled;
    private long appId;

    @Column(nullable = false)
    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(final long sectionId) {
        this.sectionId = sectionId;
    }

    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
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
    public long getAppId() {
        return appId;
    }

    public void setAppId(final long appId) {
        this.appId = appId;
    }

}
