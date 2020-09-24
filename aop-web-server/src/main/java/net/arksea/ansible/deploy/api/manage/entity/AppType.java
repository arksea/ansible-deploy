package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_type")
public class AppType extends IdEntity {
    private String name;

    @Column(nullable = false, length = 64, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
