package net.arksea.ansible.deploy.api.operator.service;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.OnComplete;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationType;
import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import net.arksea.ansible.deploy.api.manage.msg.OperationVariable;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Create by xiaohaixing on 2020/9/30
 */
public class JobPlayer extends AbstractActor {
    private Logger logger = LogManager.getLogger(JobPlayer.class);
    private final OperationJob job;
    private final AppOperation operation;
    private final App app;
    private final Set<Long> hosts;
    private final Set<OperationVariable> operationVariables;
    private final LinkedList<String> logs = new LinkedList<>();
    private final JobResources beans;
    private final static long MAX_LOG_LEN_PER_REQUEST = 10240;
    private final static long STOP_JOB_DELAY = 5;
    private final static long JOB_PLAY_TIMEOUT = 1800;
    private FileWriter jobLogFileWriter;
    private boolean noMoreLogs = false;
    private IJobEventListener listener;
    private JobCommandRunner jobCommandRunner;
    private boolean operationJobFailed = false;
    private boolean hasClientPollingLog = false; //有客户端正在实时读取日志，退出消息将会被延迟

    private JobPlayer(OperationJob job, Set<Long>hosts, Set<OperationVariable> operationVariables, JobResources beans) {
        this.job = job;
        this.hosts = hosts;
        this.operationVariables = operationVariables;
        this.beans = beans;
        this.operation = beans.appOperationDao.findOne(job.getOperationId());
        this.app = beans.appDao.findOne(job.getAppId());
        String cmd = getJobPath() + operation.getCommand();
        ActorRef self = self();
        this.listener = new IJobEventListener() {
            @Override
            public void log(String str) {
                self.tell(new OfferLog(str), ActorRef.noSender());
            }

            @Override
            public void onFinished() {
                delayStopJob();
                self().tell(new NoMoreLogs(), ActorRef.noSender());
            }
        };
        jobCommandRunner = new JobCommandRunner(cmd, listener);
    }

    static Props props(OperationJob job, Set<Long>hosts, Set<OperationVariable> operationVariables, JobResources state) {
        return Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = -4530785006800458792L;
            @Override
            public Actor create() {
                return new JobPlayer(job, hosts, operationVariables, state);
            }
        });
    }

    private static class Init {}
    static class PollLogs {
        final int index;
        PollLogs(int index) {
            this.index = index;
        }
    }
    public static class PollLogsResult {
        public String log;
        public int index;
        public int size;
        private PollLogsResult() {}
        PollLogsResult(String log, int index, int size) {
            this.log = log;
            this.index = index;
            this.size = size;
        }
    }
    private static class OfferLog {
        final String log;
        OfferLog(String log) {
            this.log = log;
        }
    }
    private static class OfferLogs {
        final List<String> logs;
        OfferLogs(List<String> logs) {
            this.logs = logs;
        }
    }
    private static class NoMoreLogs {}
    public static class StopJob {}
    private static class StartJob{}

    public void preStart() {
        context().system().scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                self(),new Init(),context().dispatcher(),self());
        context().system().scheduler().scheduleOnce(
                Duration.create(JOB_PLAY_TIMEOUT, TimeUnit.SECONDS),
                self(),new StopJob(),context().dispatcher(),self());
    }

    public void postStop() {
        saveJobInfo();
        releaseOperationToken();
        closeJobLogFile();
        sendJobNotificationMail();
    }

    private void saveJobInfo() {
        try {
            job.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (operation.getType() != AppOperationType.STATUS) { //为了减少日志占用数据库空间，状态查询日志只记录到文件
                StringBuilder sb = new StringBuilder();
                //logs.forEach(sb::append);
                //这里曾经发生过一次异常错误
                //判断不出确切原因，难道有其他异常被这个空指针异常抑制掉了看不到？
                //先改成传统写法，saveJobInfo()加了异常捕获，再观察
                //10:09:30.995 | ERROR | null | JobPlayer.$anonfun$applyOrElse$1(69) | system-akka.actor.default-dispatcher-2010 | akka://system/user/operationJob3185
                //java.lang.NullPointerException
                //        ... suppressed 2 lines
                //        at net.arksea.ansible.deploy.api.operator.service.JobPlayer.saveJobInfo(JobPlayer.java:142) ~[classes/:?]
                //        at net.arksea.ansible.deploy.api.operator.service.JobPlayer.postStop(JobPlayer.java:133) ~[classes/:?]
                //        at akka.actor.Actor.aroundPostStop(Actor.scala:536) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.Actor.aroundPostStop$(Actor.scala:536) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.AbstractActor.aroundPostStop(AbstractActor.scala:147) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.dungeon.FaultHandling.finishTerminate(FaultHandling.scala:210) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.dungeon.FaultHandling.terminate(FaultHandling.scala:172) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.dungeon.FaultHandling.terminate$(FaultHandling.scala:142) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.ActorCell.terminate(ActorCell.scala:433) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.ActorCell.invokeAll$1(ActorCell.scala:531) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.actor.ActorCell.systemInvoke(ActorCell.scala:547) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.Mailbox.processAllSystemMessages(Mailbox.scala:282) ~[akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.Mailbox.processMailbox(Mailbox.scala:260) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.Mailbox.run(Mailbox.scala:224) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.Mailbox.exec(Mailbox.scala:234) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979) [akka-actor_2.12-2.5.11.jar:2.5.11]
                //        at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107) [akka-actor_2.12-2.5.11.jar:2.5.11]
                for (String l : this.logs) {
                    sb.append(l);
                }
                job.setLog(sb.toString());
                beans.operationJobDao.save(job);
            }
        } catch (Exception ex) {
            logger.error("保存操作日志失败",ex);
        }
    }

    private void releaseOperationToken() {
        try {
            OperationToken t = beans.operationTokenDao.findByAppId(job.getAppId());
            if (t != null) {
                t.setReleased(true);
                t.setReleaseTime(new Timestamp(System.currentTimeMillis()));
                beans.operationTokenDao.save(t);
            }
        } catch (Exception ex) {
            logger.error("释放操作令牌失败", ex);
        }
    }

    private void sendJobNotificationMail() {
        try {
            if (job.getTriggerId() != null) {
                OperationTrigger t = beans.operationTriggerDao.findOne(job.getTriggerId());
                if (t != null && StringUtils.isNotBlank(t.getNotifyEmails())) {
                    boolean matched = false;
                    if (StringUtils.isNotBlank(t.getNotifyRegex())) {
                        try {
                            Pattern pattern = Pattern.compile(t.getNotifyRegex(), Pattern.MULTILINE);
                            matched = pattern.matcher(job.getLog()).find();
                        } catch (Exception ex) {
                            logger.warn("用正则搜索失败，regex={}", t.getNotifyRegex());
                        }
                    }
                    String emails = t.getNotifyEmails();
                    logger.debug("operationJobFailed={}, matched={}, notifyMatchOrNot={}", operationJobFailed, matched, t.getNotifyMatchOrNot());
                    if (operationJobFailed || t.getNotifyMatchOrNot().equals(matched)) {
                        String subject = operation.getName() + "" + app.getApptag();
                        String html = "<pre>\n" + job.getLog() + "\n</pre>";
                        beans.mailService.send(emails, subject, html);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("发送通知邮件失败", ex);
        }
    }

    private void closeJobLogFile() {
        if (jobLogFileWriter != null) {
            try {
                jobLogFileWriter.flush();
                jobLogFileWriter.close();
            } catch (Exception ex) {
                logger.warn("关闭操作结果日志文件失败", ex);
            }
        }
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Init.class,      this::handleInit)
                .match(PollLogs.class,  this::handlePollLogs)
                .match(OfferLog.class,  this::handleOfferLog)
                .match(OfferLogs.class, this::handleOfferLogs)
                .match(StartJob.class,  this::handleStartJob)
                .match(NoMoreLogs.class,this::handleNoMoreLogs)
                .match(StopJob.class,   this::handleStopJob)
                .build();
    }

    private void handleInit(Init msg) {
        try {
            String path = getJobPath() + "job-play.log";
            final File file = new File(path);
            final File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            jobLogFileWriter = new FileWriter(file);
            offerLog("初始化操作任务:\n");
            JobContextCreator creator = new JobContextCreator(getJobPath(), job, operation, hosts, operationVariables, beans, this::offerLog);
            creator.run();
            self().tell(new StartJob(), self());
        } catch (Exception ex) {
            delayStopJob();
            offerLog("初始化操作任务失败: "+ex.getMessage() + "\n");
            operationJobFailed = true;
            logger.warn("初始化操作任务失败",ex);
            self().tell(new NoMoreLogs(), ActorRef.noSender());
        }
    }

    private void handleStartJob(StartJob msg) {
        Futures.future(() -> {
            jobCommandRunner.exec();
            return true;
        }, context().dispatcher()).onComplete(new OnComplete<Boolean>() {
            @Override
            public void onComplete(Throwable failure, Boolean success) {
                delayStopJob();
                if (failure == null) {
                    offerLog("操作任务完成\n");
                } else {
                    offerLog("操作任务失败:"+failure.getMessage()+"\n");
                    operationJobFailed = true;
                    logger.warn("操作任务失败", failure);
                }
                self().tell(new NoMoreLogs(), ActorRef.noSender());
            }
        }, context().dispatcher());
    }

    private void delayStopJob() {
        context().system().scheduler().scheduleOnce(
                Duration.create(STOP_JOB_DELAY, TimeUnit.SECONDS),
                self(),new StopJob(),context().dispatcher(),self());
    }

    private void handleNoMoreLogs(NoMoreLogs msg) {
        this.noMoreLogs = true;
    }

    private void handlePollLogs(PollLogs msg) {
        if (msg.index < logs.size() || !noMoreLogs) {
            hasClientPollingLog = true;
            StringBuilder sb = new StringBuilder();
            long len = 0;
            int index;
            for (index = msg.index; index < logs.size(); ++index) {
                String log = logs.get(index);
                sb.append(log);
                len = len + log.length();
                if (len > MAX_LOG_LEN_PER_REQUEST) {
                    ++index;
                    break;
                }
            }
            PollLogsResult result = new PollLogsResult(sb.toString(), index, logs.size());
            sender().tell(result, self());
        } else {
            hasClientPollingLog = false;
            PollLogsResult result = new PollLogsResult("", -1, logs.size());
            sender().tell(result, self());
        }
    }

    private void handleStopJob(StopJob msg) {
        if (hasClientPollingLog) {
            delayStopJob();
        } else {
            jobCommandRunner.destroy();
            context().stop(self());
        }
    }
    private void handleOfferLog(OfferLog msg) {
        offerLog(msg.log);
    }
    private void handleOfferLogs(OfferLogs msg) {
        for(String log : msg.logs) {
            offerLog(log);
        }
    }
    private void offerLog(String log) {
        try {
            this.logs.offer(log);
            jobLogFileWriter.append(log);
            jobLogFileWriter.flush();
        } catch (Exception ex) {
            logger.warn("记录操作结果失败", ex);
        }
    }

    private String getJobPath() {
        LocalDate localDate = LocalDate.now();
        return beans.jobWorkRoot + "/" + localDate + "/" + app.getApptag() + "/" + job.getId() + "/";
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("^BUILD SUCCESSFUL in \\d+", Pattern.MULTILINE);
        String log = "Use '--warning-mode all' to show the individual deprecation warnings.\n" +
                "See https://docs.gradle.org/6.8.2/userguide/command_line_interface.html#sec:command_line_warnings\n" +
                "\n" +
                "BUILD SUCCESSFUL in 13s\n" +
                "4 actionable tasks: 4 executed\n" +
                "Sending build context to Docker daemon  243.9MB";
        boolean matched = pattern.matcher(log).find();
        System.out.println(matched);
    }
}
