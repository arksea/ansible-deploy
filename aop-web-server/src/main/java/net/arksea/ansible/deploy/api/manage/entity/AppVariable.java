package net.arksea.ansible.deploy.api.manage.entity;
import javax.persistence.*;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name = "dp2_app_variable",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appId", "name" })
)
public class AppVariable extends IdEntity {

    private Long appId; // 所属应用
    private String name;// 变量名
    private String value;// 变量值
    private Boolean isPort;// 是否端口值，用于主机范围的唯一性判断

    @Column(nullable = false)
    public Long getAppId() {
        return appId;
    }

    public void setAppId(final Long appId) {
        this.appId = appId;
    }

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Column(length = 256, nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    public Boolean getIsPort() {
        return isPort;
    }

    public void setIsPort(final Boolean isPort) {
        this.isPort = isPort;
    }
}
