package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.manage.dao.OperationTriggerDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import net.arksea.ansible.deploy.api.manage.entity.Version;
import net.arksea.ansible.deploy.api.manage.msg.OperationVariable;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.service.JobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

/**
 * Create by xiaohaixing on 2020/9/14
 */
@Component
public class TriggerService {
    private static final Logger logger = LogManager.getLogger(TriggerService.class);
    @Autowired
    OperationTriggerDao operationTriggerDao;
    @Autowired
    JobService jobService;
    @Autowired
    VersionDao versionDao;

    @Transactional
    public OperationTrigger saveTrigger(ClientInfo info, OperationTrigger trigger) {
        if (trigger.getId() == null) {
            trigger.setCreateUserId(info.userId);
            trigger.setCreateUser(info.username);
            trigger.setCreateTime(new Timestamp(System.currentTimeMillis()));
            String token = UUID.randomUUID().toString();
            trigger.setToken(token);
        }
        return operationTriggerDao.save(trigger);
    }

    @Transactional
    public void delTrigger(long triggerId) {
        operationTriggerDao.delete(triggerId);
    }

    public long onTrigger(String projectTag, String token, Set<OperationVariable> vars) {
        OperationTrigger trigger = operationTriggerDao.findByProjectTagAndToken(projectTag, token);
        if (trigger == null) {
            throw new ServiceException("Not found the trigger or invalid token");
        } else {
            Version ver = versionDao.findOne(trigger.getVersionId());
            if (ver == null) {
                throw new ServiceException("Not fond the trigger's version: " + trigger.getId());
            } else {
                OperationJob job = jobService.create(trigger.getCreateUserId(), ver.getAppId(),
                        trigger.getVersionId(), trigger.getOperationId(), trigger.getId());
                Set<Long> hosts = new HashSet<>();
                ver.getTargetHosts().forEach(h -> hosts.add(h.getId()));
                jobService.startJob(job, hosts, vars);
                return job.getId();
            }
        }
    }
}
