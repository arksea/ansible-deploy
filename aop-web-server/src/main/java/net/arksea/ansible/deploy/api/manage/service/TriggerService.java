package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.manage.dao.OperationTriggerDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import net.arksea.ansible.deploy.api.manage.entity.Version;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.service.JobService;
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
    @Autowired
    OperationTriggerDao operationTriggerDao;
    @Autowired
    JobService jobService;
    @Autowired
    VersionDao versionDao;

    @Transactional
    public OperationTrigger addTrigger(ClientInfo info, OperationTrigger trigger) {
        trigger.setCreateUserId(info.userId);
        trigger.setCreateUser(info.username);
        trigger.setCreateTime(new Timestamp(System.currentTimeMillis()));
        String token = UUID.randomUUID().toString();
        trigger.setToken(token);
        return operationTriggerDao.save(trigger);
    }

    @Transactional
    public void delTrigger(long triggerId) {
        operationTriggerDao.delete(triggerId);
    }

    public long onTrigger(String token) {
        List<OperationTrigger> list = operationTriggerDao.findByToken(token);
        if (list.size() > 0) {
            OperationTrigger trigger = list.get(0);
            Version ver = versionDao.findOne(trigger.getVersionId());
            if (ver != null) {
                OperationJob job = jobService.create(trigger.getCreateUserId(), ver.getAppId(),
                        trigger.getVersionId(), trigger.getOperationId(), trigger.getId());
                Set<Long> hosts = new HashSet<>();
                ver.getTargetHosts().forEach(h -> hosts.add(h.getId()));
                jobService.startJob(job, hosts);
                return job.getId();
            }
        }
        return -1;
    }
}
