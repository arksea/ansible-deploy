package net.arksea.ansible.operator.api.auth.service;


import net.arksea.ansible.operator.api.auth.entity.OperationTypeEnum;
import net.arksea.ansible.operator.api.auth.entity.ResourceTypeEnum;
import net.arksea.ansible.operator.api.auth.info.ClientInfo;

public interface IOperationLogService {
    void addOperationLog(ClientInfo clientInfo, OperationTypeEnum operationType, ResourceTypeEnum resourceType, Long resourceId, String originalContent, String modifiedContent, Boolean succeed);
}
