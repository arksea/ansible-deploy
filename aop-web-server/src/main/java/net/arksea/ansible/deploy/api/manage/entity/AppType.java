package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_type")
public class AppType extends IdEntity {
    private String name;
    private String description;

    @Column(nullable = false, length = 64, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 256, unique = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
