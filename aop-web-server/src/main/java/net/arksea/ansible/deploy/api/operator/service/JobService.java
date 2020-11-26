package net.arksea.ansible.deploy.api.operator.service;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import net.arksea.ansible.deploy.api.ServiceException;
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
import scala.concurrent.Future;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;

import static akka.japi.Util.classTag;

/**
 * Create by xiaohaixing on 2020/9/30
 */
@Component
public class JobService {
    private static final Logger logger = LogManager.getLogger(JobService.class);
    private static final long HOLD_TIMEOUT_SECONDS = 600;
    @Autowired
    private OperationJobDao operationJobDao;
    @Autowired
    private OperationTokenDao operationTokenDao;
    @Autowired
    ActorSystem system;
    @Resource(name = "systemBindPort")
    int systemBindPort;
    @Autowired
    JobResources jobResources;

    @Transactional
    public OperationJob create(long userId, long appId, Long versionId, long operationId) {
        OperationToken t = operationTokenDao.findByAppId(appId);
        if (t == null) {
            t = new OperationToken();
            t.setAppId(appId);
            t.setReleased(true);
            t = operationTokenDao.save(t);
        }
        OperationJob job = new OperationJob();
        job.setAppId(appId);
        job.setVersionId(versionId);
        job.setOperatorId(userId);
        job.setOperationId(operationId);
        job.setExecHost(getLocalHost());
        job.setStartTime(new Timestamp(System.currentTimeMillis()));
        OperationJob saved = operationJobDao.save(job);
        int holded = operationTokenDao.hold(job.getAppId(), job.getOperatorId(), saved.getId(), HOLD_TIMEOUT_SECONDS);
        if (holded == 0) {
            User user = t.getHolder();
            if (user == null) {
                throw new ServiceException("操作权限未释放");
            } else {
                throw new ServiceException("["+user.getName()+"]正在操作,请稍后再试");
            }
        } else if (holded > 1) {
            throw new ServiceException("断言失败");
        }
        return saved;
    }

    public void startJob(OperationJob job, Set<Long> hosts) {
        String name = makeJobActorName(job.getId());
        ActorRef ref = system.actorOf(JobPlayer.props(job, hosts, jobResources), name);
    }

    public Future<JobPlayer.PollLogsResult> pollJobLogs(long jobId, int index) {
        OperationJob job = operationJobDao.findOne(jobId);
        if (job.getEndTime() == null) {
            String path = "akka.tcp://system@" + job.getExecHost() + ":" + systemBindPort + "/user/" + makeJobActorName(jobId);
            ActorSelection s = system.actorSelection(path);
            JobPlayer.PollLogs msg = new JobPlayer.PollLogs(index);
            return Patterns.ask(s, msg, 10000).mapTo(classTag(JobPlayer.PollLogsResult.class));
        } else {
            return Futures.successful(new JobPlayer.PollLogsResult("", -1, 0));
        }
    }

    private String makeJobActorName(long jobId) {
        return "operationJob"+ jobId;
    }

    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            logger.warn("获取主机地址失败", ex);
            return "localhost";
        }
    }

    @Transactional
    public void clearJobRecords(long logExpireDays) {
        LocalDate localDate = LocalDate.now().minusDays(logExpireDays);
        long epochSecond = localDate.atStartOfDay().toEpochSecond(ZoneOffset.of("+8"));
        int n = operationJobDao.deleteExpireJobs(new Timestamp(epochSecond*1000));
        logger.info("删除 {} 前的操作记录 {} 条", localDate, n);
    }
}
