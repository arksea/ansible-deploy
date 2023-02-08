package net.arksea.ansible.deploy.api.manage.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Create by xiaohaixing on 2020/11/19
 */
@Entity
@Table(name = "sys_properties")
public class SystemProperty extends IdEntity {
    private String name;
    private String value;

    @NotBlank
    @Column(length = 32, nullable = false)
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Column(length = 64, nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
