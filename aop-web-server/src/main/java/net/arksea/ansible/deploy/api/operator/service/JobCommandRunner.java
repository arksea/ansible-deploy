package net.arksea.ansible.deploy.api.operator.service;

import akka.dispatch.Futures;
import akka.dispatch.OnComplete;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;

/**
 * Create by xiaohaixing on 2020/10/12
 */
public class JobCommandRunner {
    private Logger logger = LogManager.getLogger(JobCommandRunner.class);
    private JobResources resources;
    private OperationJob job;
    private IJobEventListener jobLogger;

    public JobCommandRunner(OperationJob job, JobResources resources, IJobEventListener jobLogger) {
        this.job = job;
        this.resources = resources;
        this.jobLogger = jobLogger;
    }

    public void run() {
        try {
            log("启动操作任务:\n");
            AppOperation op = resources.appOperationDao.findOne(job.getOperationId());
            String cmd = getJobPath()+op.getCommand();
            Futures.future(() -> {
                exec(cmd);
                return true;
            }, resources.system.dispatcher()).onComplete(new OnComplete<Boolean>() {
                @Override
                public void onComplete(Throwable failure, Boolean success) {
                    if (failure == null) {
                        log("操作任务完成\n");
                    } else {
                        log("操作任务失败:"+failure.getMessage()+"\n");
                        logger.warn("操作任务失败", failure);
                    }
                    jobLogger.onFinished();
                }
            }, resources.system.dispatcher());
        } catch (Exception ex) {
            log("操作任务失败:"+ex.getMessage()+"\n");
            logger.warn("操作任务失败", ex);
        }
    }

    private void log(String str) {
        jobLogger.log(str);
    }

    protected void exec(final String cmd) throws IOException {
        final Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            log("执行启动命令行:\n");
            String line = inReader.readLine();
            while (line != null) {
                log(line+"\n");
                line = inReader.readLine();
            }
            line = errReader.readLine();
            while (line != null) {
                log(line+"\n");
                line = errReader.readLine();
            }
        } catch (Exception ex) {
            log("执行启动命令行失败\n");
        } finally {
            process.destroy();
        }
    }

    private String getJobPath() {
        return resources.getJobPath(job);
    }
}
