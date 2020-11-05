package net.arksea.ansible.deploy.api.operator.service;

import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;

import java.io.*;
import java.util.Set;

/**
 *
 * @author xiaohaixing
 */
public class JobContextCreator {

    private JobResources resources;
    private OperationJob job;
    private Set<Long> hosts;
    private IJobEventListener jobLogger;

    public JobContextCreator(OperationJob job, Set<Long> hosts, JobResources resources, IJobEventListener jobLogger) {
        this.job = job;
        this.hosts = hosts;
        this.resources = resources;
        this.jobLogger = jobLogger;
    }

    public void run() throws Exception {
        generateCodeFiles();
        generateHostFile();
        generateVarFile();
    }

    private void log(String str) {
        jobLogger.log(str);
    }

    private String getJobPath() {
        return resources.getJobPath(job);
    }

    private void generateCodeFiles() throws IOException {
        log("生成脚本文件...");
        AppOperation op = resources.appOperationDao.findOne(job.getOperationId());
        for (AppOperationCode c : op.getCodes()) {
            generateCodeFile(c);
        }
        log("成功\n");
    }
    private void generateCodeFile(AppOperationCode code) throws IOException {
        String path = getJobPath() + code.getFileName();
        final File file = new File(path);
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (final FileWriter writer = new FileWriter(file)) {
            writer.append(code.getCode());
            writer.flush();
        }
        chmod(code.getFileName());
    }

    protected void chmod(final String fileName) throws IOException {
        String cmd = "chmod u+x " + getJobPath() + fileName;
        final Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
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
        } finally {
            process.destroy();
        }
    }

    /**
     * 生成应用的待部署目标Host文件
     */
    private void generateHostFile() throws IOException {
        log("生成hosts文件...");
        final File file = new File(getJobPath() + "/hosts");
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (final FileWriter writer = new FileWriter(file, true)) {
            writer.append("\n[deploy_target]\n");
            for (final Long hid : hosts) {
                Host h = resources.hostService.findOne(hid);
                if (h == null) {
                    log("The specified host: "+hid + " not found");
                } else {
                    writer.append(h.getPrivateIp());
                    writer.append("\n");
                }
            }
            writer.flush();
        }
        log("成功\n");
    }

    /**
     * 生成应用变量文件
     */
    private void generateVarFile() throws IOException {
        log("生成变量文件'vars.yml'...");
        final File file = new File(getJobPath() + "/vars.yml");
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (final FileWriter writer = new FileWriter(file)) {
            final App app = resources.appDao.findOne(job.getAppId());
            writer.append("apptag: ");
            writer.append(app.getApptag());
            writer.append("\n");
            for (final AppVariable var : app.getVars()) {
                writer.append(var.getName());
                writer.append(": ");
                writer.append(var.getValue());
                writer.append("\n");
            }
            writer.flush();
            final Version ver = resources.versionDao.findOne(job.getVersionId());
            writer.append("exec_opt: ");
            writer.append(ver.getExecOpt());
            writer.append("\n");
            writer.append("repository: ");
            writer.append(ver.getRepository());
            writer.append("\n");
        }
        log("成功\n");
    }

}
