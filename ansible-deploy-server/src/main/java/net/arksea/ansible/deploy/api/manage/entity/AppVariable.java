package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name = "dp2_app_variable",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appId", "name" })
)
public class AppVariable extends Variable {

    private Long appId; // 所属应用

    @Column(nullable = false)
    public Long getAppId() {
        return appId;
    }

    public void setAppId(final Long appId) {
        this.appId = appId;
    }

}
