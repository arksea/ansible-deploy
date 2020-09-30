package net.arksea.ansible.deploy.api.operator.service;

import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.operator.dao.OperationJobDao;
import net.arksea.ansible.deploy.api.operator.dao.OperationTokenDao;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Create by xiaohaixing on 2020/9/30
 */
@Component
public class JobService {
    private static final Logger logger = LogManager.getLogger(JobService.class);
    private static final String jobRoot = "/data/ansible-deploy/jobs";
    private static final long HOLD_TIMEOUT_SECONDS = 600;
    @Autowired
    private OperationJobDao operationJobDao;
    @Autowired
    private OperationTokenDao operationTokenDao;

    @Transactional
    public void create(OperationJob job) {
        OperationToken t = operationTokenDao.findByAppId(job.getAppId());
        if (t == null) {
            t = new OperationToken();
            t.setAppId(job.getAppId());
            operationTokenDao.save(t);
        }
        job.setExecHost(getLocalHost());
        job.setStartTime(new Timestamp(System.currentTimeMillis()));
        OperationJob saved = operationJobDao.save(job);
        int holded = operationTokenDao.hold(job.getAppId(), job.getOperatorId(), saved.getId(), HOLD_TIMEOUT_SECONDS);


    }

    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            logger.warn("获取主机地址失败", ex);
            return "localhost";
        }
    }

    public static String makeJobWorkPath(OperationJob job) {
        long epochDay = job.getStartTime().getTime()/3600_000L/24L;
        LocalDate localDate = LocalDate.ofEpochDay(epochDay);
        return jobRoot + "/work/" + localDate.toString() + "/" + job.getId();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis()/1000);
        OperationJob job = new OperationJob();
        job.setId(12345L);
        job.setStartTime(new Timestamp(System.currentTimeMillis()));
        job.setExecHost("127.0.0.1");
        job.setAppId(111L);
        job.setOperatorId(222L);
        job.setOperationId(333L);
        System.out.println(makeJobWorkPath(job));
    }
}
