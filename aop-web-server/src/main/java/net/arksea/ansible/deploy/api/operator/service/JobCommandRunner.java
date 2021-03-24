package net.arksea.ansible.deploy.api.operator.service;

import java.io.*;

/**
 * Create by xiaohaixing on 2020/10/12
 */
public class JobCommandRunner {
    private Process process;
    private final String cmd;
    private final IJobEventListener jobLogger;
    private final String[] envp;

    public JobCommandRunner(final String cmd, IJobEventListener jobLogger) {
        this.cmd = cmd;
        this.jobLogger = jobLogger;
        this.envp = null;
    }

    public JobCommandRunner(final String cmd, IJobEventListener jobLogger, String[] envp) {
        this.cmd = cmd;
        this.jobLogger = jobLogger;
        this.envp = envp;
    }

    public void exec() throws Exception {
        this.process = Runtime.getRuntime().exec(cmd, envp);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            jobLogger.log(cmd+"\n");
            String line = inReader.readLine();
            while (line != null) {
                jobLogger.log(line+"\n");
                line = inReader.readLine();
            }
            line = errReader.readLine();
            while (line != null) {
                jobLogger.log(line+"\n");
                line = errReader.readLine();
            }
        } catch (Exception ex) {
            jobLogger.log("执行任务启动命令行失败\n");
            throw ex;
        } finally {
            jobLogger.onFinished();
            destroy();
        }
    }

    public void destroy() {
        if (process != null) {
            process.destroy();
        }
    }
}
