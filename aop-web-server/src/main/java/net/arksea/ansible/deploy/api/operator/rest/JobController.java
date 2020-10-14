package net.arksea.ansible.deploy.api.operator.rest;

import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.UserService;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.ansible.deploy.api.operator.service.JobPlayer;
import net.arksea.ansible.deploy.api.operator.service.JobService;
import net.arksea.restapi.ErrorResult;
import net.arksea.restapi.RestResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import scala.Function1;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 *
 * Created by xiaohaixing on 2020/09/25.
 */
@RestController
@RequestMapping(value = "/api/jobs")
public class JobController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    JobService jobService;
    @Autowired
    UserService userService;
    @Autowired
    ActorSystem system;

    public static class StartOpeartionJob {
        public Long appId;
        public Long operationId;
        public Set<Long> hosts;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<OperationJob> startJob(@RequestBody final StartOpeartionJob body,
                                              final HttpServletRequest httpRequest) {
        ClientInfo info = userService.getClientInfo(httpRequest);
        OperationJob job = jobService.create(info.userId, body.appId, body.operationId);
        jobService.startJob(job, body.hosts);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, job, reqid);
    }

    @RequestMapping(path="{jobId}/logs/{index}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public DeferredResult<RestResult<JobPlayer.PollLogsResult>> pollJobLogs(@PathVariable(name="jobId") final long jobId,
                                                                            @PathVariable(name="index") final int index,
                                                                            final HttpServletRequest httpRequest) {
        DeferredResult<RestResult<JobPlayer.PollLogsResult>> result = new DeferredResult<>();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        jobService.pollJobLogs(jobId, index).onComplete(
                new OnComplete<JobPlayer.PollLogsResult>() {
                    @Override
                    public void onComplete(Throwable failure, JobPlayer.PollLogsResult ret) {
                        if (failure == null) {
                            result.setResult(new RestResult<>(0, ret, reqid));
                        } else {
                            result.setErrorResult(new ErrorResult<>(1, reqid, failure.getMessage()));
                        }
                    }
                }, system.dispatcher()
        );
        return result;
    }


}
