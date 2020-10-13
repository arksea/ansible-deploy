package net.arksea.ansible.deploy.api.operator.service;

import akka.actor.ActorSystem;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.dao.HostDao;
import net.arksea.ansible.deploy.api.operator.dao.OperationJobDao;
import net.arksea.ansible.deploy.api.operator.dao.OperationTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Create by xiaohaixing on 2020/10/10
 */
@Component
public class JobResources {
    @Autowired
    public HostDao hostDao;
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
