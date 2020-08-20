package net.arksea.ansible.deploy.api.auth.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "sys_operation_log", indexes = {})
public class OperationLog extends IdEntity {

    private Long userId;                //用户ID
    private String userName;            //用户昵称
    private String remoteAddress;       //用户IP地址

    private Integer resourceType;       //操作的资源类型
    private Long resourceId;            //操作的资源的ID
    private Integer operationType;       //操作类型

    private String originalContent;     //原内容
    private String modifiedContent;     //修改的内容
    private  boolean succeed;           //操作是否成功
    private Timestamp operationTime;    //操作时间

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getModifiedContent() {
        return modifiedContent;
    }

    public void setModifiedContent(String modifiedContent) {
        this.modifiedContent = modifiedContent;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public Timestamp getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Timestamp operationTime) {
        this.operationTime = operationTime;
    }
}
