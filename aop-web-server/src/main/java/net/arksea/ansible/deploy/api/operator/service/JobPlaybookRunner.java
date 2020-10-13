package net.arksea.ansible.deploy.api.operator.service;

import akka.dispatch.Futures;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;

import java.io.*;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/10/12
 */
public class JobPlaybookRunner {
    private JobResources resources;
    private OperationJob job;
    private Set<Long> hosts;
    private IJobLogger jobLogger;

    public JobPlaybookRunner(OperationJob job, Set<Long> hosts, JobResources resources, IJobLogger jobLogger) {
        this.job = job;
        this.hosts = hosts;
        this.resources = resources;
        this.jobLogger = jobLogger;
    }

    public void run() {
        try {
            log("开始操作任务:\n");
            String cmd = "";
            Futures.future(() -> {
                exec(cmd);
                return true;
            }, resources.system.dispatcher());
            log("操作任务完成\n");
        } catch (Exception ex) {
            log("操作任务失败\n");
        }
    }

    private void log(String str) {
        jobLogger.log(str);
    }

    protected void exec(final String cmd) throws IOException {
        final Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            log("执行Playbook脚本:\n");
            String line = inReader.readLine();
            while (line != null) {
                log(line);
                line = inReader.readLine();
            }
            line = errReader.readLine();
            while (line != null) {
                log(line);
                line = errReader.readLine();
            }
            log("执行Playbook脚本完成\n");
        } catch (Exception ex) {
            log("执行Playbook脚本失败\n");
        } finally {
            process.destroy();
        }
    }
}
