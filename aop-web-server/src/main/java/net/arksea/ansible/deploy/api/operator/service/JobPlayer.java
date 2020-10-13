package net.arksea.ansible.deploy.api.operator.service;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.entity.OperationToken;
import org.apache.commons.lang3.tuple.Pair;
import scala.concurrent.duration.Duration;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Create by xiaohaixing on 2020/9/30
 */
public class JobPlayer extends AbstractActor {

    private final OperationJob job;
    private final Set<Long> hosts;
    private final LinkedList<String> logs = new LinkedList<>();
    private final JobResources beans;
    private final long MAX_LOG_LEN_PER_REQUEST = 1024;
    private final long STOP_JOB_DELAY = 10;

    private JobPlayer(OperationJob job, Set<Long>hosts, JobResources beans) {
        this.job = job;
        this.hosts = hosts;
        this.beans = beans;
    }

    public static Props props(OperationJob job, Set<Long>hosts, JobResources state) {
        return Props.create(new Creator<Actor>() {
            @Override
            public Actor create() {
                return new JobPlayer(job, hosts, state);
            }
        });
    }

    public static class Init {}
    public static class PollLogs {
        public final int index;
        public PollLogs(int index) {
            this.index = index;
        }
    }
    public static class OfferLog {
        public final String log;
        public OfferLog(String log) {
            this.log = log;
        }
    }
    public static class OfferLogs {
        public final List<String> logs;
        public OfferLogs(List<String> logs) {
            this.logs = logs;
        }
    }
    public static class StopJob {}
    public static class StartJob{}

    public void preStart() {
        context().system().scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                self(),new Init(),context().dispatcher(),self());
    }

    public void postStop() {
        job.setEndTime(new Timestamp(System.currentTimeMillis()));
        beans.operationJobDao.save(job);
        OperationToken t = beans.operationTokenDao.findByAppId(job.getAppId());
        if (t != null) {
            t.setReleased(true);
            t.setReleaseTime(new Timestamp(System.currentTimeMillis()));
            beans.operationTokenDao.save(t);
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
                .match(StopJob.class,   this::handleStopJob)
                .build();
    }

    private void handleInit(Init msg) {
        JobContextCreator creator = new JobContextCreator(job, hosts, beans, this.logs::offer);
        creator.run();
        self().tell(new StartJob(), self());
    }

    private void handleStartJob(StartJob msg) {
        ActorRef self = self ();
        JobPlaybookRunner runner = new JobPlaybookRunner(job, hosts, beans,
                str -> self.tell(new OfferLog(str), ActorRef.noSender())
        );
        runner.run();
        context().system().scheduler().scheduleOnce(
                Duration.create(STOP_JOB_DELAY, TimeUnit.SECONDS),
                self(),new StopJob(),context().dispatcher(),self());
    }

    private void handlePollLogs(PollLogs msg) {
        StringBuilder sb = new StringBuilder();
        long len = 0;
        int index = -1;
        for (index = msg.index; index < logs.size(); ++index) {
            String log = logs.get(index);
            sb.append(log);
            len = len + log.length();
            if (len > MAX_LOG_LEN_PER_REQUEST) {
                break;
            }
        }
        Pair<String,Integer> pair = Pair.of(sb.toString(), index);
        sender().tell(pair, self());
    }

    private void handleStopJob(StopJob msg) {
        context().stop(self());
    }
    private void handleOfferLog(OfferLog msg) {
        this.logs.offer(msg.log);
    }
    private void handleOfferLogs(OfferLogs msg) {
        for(String log : msg.logs) {
            this.logs.offer(log);
        }
    }
}
