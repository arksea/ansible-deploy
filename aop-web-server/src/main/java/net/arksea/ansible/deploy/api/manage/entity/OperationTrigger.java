package net.arksea.ansible.deploy.api.manage.entity;

import net.arksea.ansible.deploy.api.operator.entity.IdEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 系统在收到触发器事件时，用项目标识projectTag查询Token与请求的Token进行校验，通过则执行
 * Create by xiaohaixing on 2021/3/15
 */
@Entity
@Table(name = "dp2_operation_trigger")
public class OperationTrigger extends IdEntity {
    private Long versionId;
    private Long operationId;
    private String projectTag; //用于唯一标识项目
    private String token;
    private String description;
    private Long createUserId;
    private String createUser;
    private Timestamp createTime;
    private Timestamp expiredTime;

    @Column(nullable = false)
    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    @Column(nullable = false)
    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Column(nullable = false, length = 24)
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(nullable = false)
    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Column(nullable = false, unique = true)
    public String getProjectTag() {
        return projectTag;
    }

    public void setProjectTag(String projectTag) {
        this.projectTag = projectTag;
    }

    @Column(nullable = false, length = 64)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(length = 128)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Column(nullable = false)
    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }

}
