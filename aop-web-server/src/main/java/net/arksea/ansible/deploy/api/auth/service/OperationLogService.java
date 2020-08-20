package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.ResourceTypeEnum;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.dao.OperationLogDao;
import net.arksea.ansible.deploy.api.auth.entity.OperationLog;
import net.arksea.ansible.deploy.api.auth.entity.OperationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class OperationLogService implements IOperationLogService {
    @Autowired
    OperationLogDao logDao;

    @Override
    public void addOperationLog(ClientInfo clientInfo, OperationTypeEnum operationType, ResourceTypeEnum resourceType, Long resourceId, String originalContent, String modifiedContent, Boolean succeed) {
        OperationLog log = fillOperationLogEntity(clientInfo, operationType, resourceType, resourceId, originalContent, modifiedContent, succeed);
        logDao.save(log);
    }

    private OperationLog fillOperationLogEntity(ClientInfo clientInfo, OperationTypeEnum opType, ResourceTypeEnum resourceType
            , Long resourceId, String originalContent, String modifiedContent, Boolean succeed) {
        OperationLog log = new OperationLog();
        Long userId = null;
        String userName = "";
        String remoteAddress = "";
        if (clientInfo != null) {
            userId = clientInfo.userId;
            userName = clientInfo.username;
            remoteAddress = clientInfo.remoteAddress;
        }
        log.setUserId(userId);
        log.setUserName(userName);
        log.setRemoteAddress(remoteAddress);
        log.setOperationType(opType.key);
        log.setResourceType(resourceType.key);
        log.setResourceId(resourceId);
        log.setOriginalContent(originalContent);
        log.setModifiedContent(modifiedContent);
        log.setSucceed(succeed);
        log.setOperationTime(Timestamp.valueOf(LocalDateTime.now()));
        return log;
    }
}
