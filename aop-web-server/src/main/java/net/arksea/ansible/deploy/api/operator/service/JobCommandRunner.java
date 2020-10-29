package net.arksea.ansible.deploy.api.operator.service;

import akka.dispatch.Futures;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.concurrent.Future;

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

    public Future<Boolean> run() {
        AppOperation op = resources.appOperationDao.findOne(job.getOperationId());
        String cmd = getJobPath()+op.getCommand();
        return Futures.future(() -> {
            exec(cmd);
            return true;
        }, resources.system.dispatcher());
    }

    private void log(String str) {
        jobLogger.log(str);
    }

    protected void exec(final String cmd) throws IOException {
        final Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            log(cmd+"\n");
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
            jobLogger.onFinished();
        } catch (Exception ex) {
            log("执行任务启动命令行失败\n");
            logger.warn("执行启动命令行失败", ex);
        } finally {
            process.destroy();
        }
    }

    private String getJobPath() {
        return resources.getJobPath(job);
    }
}
