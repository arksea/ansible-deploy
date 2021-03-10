package net.arksea.ansible.deploy.api.operator.service;

import java.io.*;

/**
 * Create by xiaohaixing on 2020/10/12
 */
public class JobCommandRunner {

    protected static void exec(final String cmd, IJobEventListener jobLogger) throws Exception {
        exec(cmd, jobLogger, null);
    }

    protected static void exec(final String cmd, IJobEventListener jobLogger, String[] envp) throws Exception {
        final Process process = Runtime.getRuntime().exec(cmd, envp);
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
            process.destroy();
        }
    }
}
