package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appTypeId", "name" })
)
public class AppVarDefine extends IdEntity {

    private long appTypeId;
    private String name;// 变量名
    private PortType portType; // 是否端口值

    @Column(nullable = false)
    public long getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(long appTypeId) {
        this.appTypeId = appTypeId;
    }

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
