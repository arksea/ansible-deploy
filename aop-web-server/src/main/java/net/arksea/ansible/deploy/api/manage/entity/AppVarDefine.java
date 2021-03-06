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
}
