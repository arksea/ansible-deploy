package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2021/03/02
 */
@Entity
@Table(name = "dp2_operation_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "operationId", "name" })
)
public class OperationVarDefine extends IdEntity {

    private Long operationId;
    private String name;// 变量名
    private String formLabel="";  //表单输入框标签
    private String inputAddon=""; //表单输入框提示前缀
    private String defaultValue;
    private String value;

    @Column(nullable = false)
    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
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

    @Transient
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this.getId() == null) {
            return false;
        } else if (o instanceof OperationVarDefine) {
            OperationVarDefine def = (OperationVarDefine) o;
            return this.getId().equals(def.getId());
        } else {
            return false;
        }
    }
}
