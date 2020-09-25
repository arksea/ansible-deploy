package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "app_type_id", "name" })
)
public class AppVarDefine extends IdEntity {

    private AppType appType;
    private String name;// 变量名
    private String defaultValue;
    private PortType portType; // 是否端口值

    @ManyToOne
    @JoinColumn(name="app_type_id", nullable = false)
    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 256)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @ManyToOne
    @JoinColumn(name="port_type_id")
    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }
}
