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
@Table(name = "dp2_version_variable",
        uniqueConstraints = @UniqueConstraint(columnNames = { "versionId", "name" })
)
public class VersionVariable extends IdEntity {

    private Long versionId; // 所属版本
    private String name;// 变量名
    private String value;// 变量值
    private Boolean isPort;// 是否端口值，用于主机范围的唯一性判断

    @Column(nullable = false)
    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(final Long versionId) {
        this.versionId = versionId;
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
