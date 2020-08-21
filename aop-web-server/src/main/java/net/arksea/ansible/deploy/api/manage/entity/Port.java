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

    /**
     * <p>
     * Title: getType<／p>
     * <p>
     * Description: <／p>
     * 
     * @return
     */
    @Column(name = "type", nullable = false)
    public int getType() {
        return type;
    }

    /**
     * <p>
     * Title: setType<／p>
     * <p>
     * Description: <／p>
     * 
     * @param type
     */
    public void setType(final int type) {
        this.type = type;
    }

    /**
     * <p>
     * Title: getName<／p>
     * <p>
     * Description: <／p>
     * 
     * @return
     */
    @Column(name = "name", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Title: setName<／p>
     * <p>
     * Description: <／p>
     * 
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * <p>
     * Title: getValue<／p>
     * <p>
     * Description: <／p>
     * 
     * @return
     */
    @Column(name = "value", nullable = false)
    public int getValue() {
        return value;
    }

    /**
     * <p>
     * Title: setValue<／p>
     * <p>
     * Description: <／p>
     * 
     * @param value
     */
    public void setValue(final int value) {
        this.value = value;
    }

    /**
     * <p>
     * Title: getEnable<／p>
     * <p>
     * Description: <／p>
     * 
     * @return
     */
    @Column(name = "enable", nullable = false)
    public int getEnable() {
        return enable;
    }

    /**
     * <p>
     * Title: setEnable<／p>
     * <p>
     * Description: <／p>
     * 
     * @param enable
     */
    public void setEnable(final int enable) {
        this.enable = enable;
    }

    /**
     * <p>
     * Title: getApp<／p>
     * <p>
     * Description: <／p>
     * 
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "app_id", nullable = false)
    @JsonIgnore
    public App getApp() {
        return app;
    }

    /**
     * <p>
     * Title: setApp<／p>
     * <p>
     * Description: <／p>
     * 
     * @param app
     */
    public void setApp(final App app) {
        this.app = app;
    }

}
