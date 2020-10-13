package net.arksea.ansible.deploy.api.operator.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;

import java.io.*;
import java.util.Map;
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
            log("执行Playbook脚本:\n");
            log("执行Playbook脚本完成\n");
        } catch (Exception ex) {
            log("执行Playbook脚本失败\n");
            throw new ServiceException("执行Playbook脚本失败\n", ex);
        }
    }

    private void log(String str) {
        jobLogger.log(str);
    }

    protected String exec(final String cmd) throws IOException {
        final Process process = Runtime.getRuntime().exec(cmd);
        final ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
             OutputStreamWriter bufWriter = new OutputStreamWriter(buf);) {
            String line = inReader.readLine();
            final Long appId = job.getAppId();
            while (line != null) {
                bufWriter.write(line);
                bufWriter.write("\n");
                log(line);
                line = inReader.readLine();
            }
            line = errReader.readLine();
            while (line != null) {
                bufWriter.write(line);
                bufWriter.write("\n");
                log(line);
                line = errReader.readLine();
            }
            bufWriter.flush();
        } finally {
            process.destroy();
        }
        return buf.toString();
    }
}
