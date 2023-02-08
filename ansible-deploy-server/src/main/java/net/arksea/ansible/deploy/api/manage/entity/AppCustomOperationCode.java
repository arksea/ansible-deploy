package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2021/04/09
 */
@Entity
@Table(name = "dp2_app_custom_op_code", uniqueConstraints = @UniqueConstraint(columnNames = { "appId", "operationId", "fileName" }))
public class AppCustomOperationCode extends IdEntity {
    private Long appId;
    private Long operationId;
    private String fileName;
    private String code;

    @Column(nullable = false)
    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

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
        } else if (o instanceof AppCustomOperationCode) {
            return ((AppCustomOperationCode)o).getId().equals(this.getId());
        } else {
            return false;
        }
    }
}
