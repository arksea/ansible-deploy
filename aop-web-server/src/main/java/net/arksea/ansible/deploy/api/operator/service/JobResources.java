package net.arksea.ansible.deploy.api.operator.service;

import akka.actor.ActorSystem;
import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.service.HostsService;
import net.arksea.ansible.deploy.api.operator.dao.OperationJobDao;
import net.arksea.ansible.deploy.api.operator.dao.OperationTokenDao;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * Create by xiaohaixing on 2020/10/10
 */
@Component
public class JobResources {
    @Autowired
    public AppDao appDao;
    @Autowired
    public HostsService hostService;
    @Autowired
    public VersionDao versionDao;
    @Autowired
    AppOperationDao appOperationDao;
    @Autowired
    OperationJobDao operationJobDao;
    @Autowired
    OperationTokenDao operationTokenDao;
    @Value("${job.workRoot}")
    String jobWorkRoot;
    @Resource(name="systemBindPort")
    int systemBindPort;
    @Autowired
    ActorSystem system;
}
