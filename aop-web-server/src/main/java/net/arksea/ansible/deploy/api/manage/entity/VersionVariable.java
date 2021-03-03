package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name = "dp2_version_variable",
        uniqueConstraints = @UniqueConstraint(columnNames = { "versionId", "name" })
)
public class VersionVariable extends Variable {
    private Long versionId; // 所属版本

    @Column(nullable = false)
    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(final Long versionId) {
        this.versionId = versionId;
    }
}
