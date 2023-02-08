package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Create by xiaohaixing on 2021/03/02
 */
@MappedSuperclass
public class VarDefine extends IdEntity {

    private Long appTypeId;
    private String name;// 变量名
    private String formLabel="";  //表单输入框标签
    private String inputAddon=""; //表单输入框提示前缀
    private String defaultValue;
    private PortType portType; // 是否端口值

    @Column(nullable = false)
    public Long getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(Long appTypeId) {
        this.appTypeId = appTypeId;
    }

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 64, nullable = false)
    public String getFormLabel() {
        return formLabel;
    }

    public void setFormLabel(String formLabel) {
        this.formLabel = formLabel;
    }

    @Column(length = 32)
    public String getInputAddon() {
        return inputAddon;
    }

    public void setInputAddon(String inputAddon) {
        this.inputAddon = inputAddon;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this.getId() == null) {
            return false;
        } else if (o instanceof VarDefine) {
            VarDefine def = (VarDefine) o;
            return this.getId().equals(def.getId());
        } else {
            return false;
        }
    }
}
