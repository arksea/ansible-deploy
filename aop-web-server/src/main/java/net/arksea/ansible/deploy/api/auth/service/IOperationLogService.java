package net.arksea.ansible.deploy.api.auth.service;


import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.entity.OperationTypeEnum;
import net.arksea.ansible.deploy.api.auth.entity.ResourceTypeEnum;

public interface IOperationLogService {
    void addOperationLog(ClientInfo clientInfo, OperationTypeEnum operationType, ResourceTypeEnum resourceType, Long resourceId, String originalContent, String modifiedContent, Boolean succeed);
}
