package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@Entity
@Table(name = "dp2_app_operation_code", uniqueConstraints = @UniqueConstraint(columnNames = { "operationId", "fileName" }))
public class AppOperationCode extends IdEntity {
    private Long operationId;
    private String fileName;
    private String code;

    @Column(nullable = false)
    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Column(length = 32, nullable = false)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    @Basic(fetch= FetchType.LAZY)
    @Lob
    @Column(nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AppOperationCode) {
            return ((AppOperationCode)o).getId().equals(this.getId());
        } else {
            return false;
        }
    }
}
