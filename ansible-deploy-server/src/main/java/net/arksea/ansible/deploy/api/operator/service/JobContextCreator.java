package net.arksea.ansible.deploy.api.operator.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.ansible.deploy.api.manage.msg.OperationVariable;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.io.*;
import java.util.*;

/**
 *
 * @author xiaohaixing
 */
public class JobContextCreator {

    private final JobResources resources;
    private final OperationJob job;
    private final AppOperation operation;
    private final Set<Long> hosts;
    private final Set<OperationVariable> operationVariables;
    private final IJobEventListener jobLogger;
    private final String jobPath;

    public JobContextCreator(String jobPath, OperationJob job, AppOperation op, Set<Long> hosts, Set<OperationVariable> operationVariables, JobResources resources, IJobEventListener jobLogger) {
        this.jobPath = jobPath;
        this.job = job;
        this.operation = op;
        this.hosts = hosts;
        this.operationVariables = operationVariables;
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
        return jobPath;
    }

    private void generateCodeFiles() throws IOException {
        log("生成脚本文件...");
        for (AppOperationCode c : operation.getCodes()) {
            generateCodeFile(c.getFileName(), c.getCode());
        }
        List<AppCustomOperationCode> codes = resources.appCodeDao.findByAppId(job.getAppId());
        for (AppCustomOperationCode c: codes) {
            if (Objects.equals(c.getOperationId(), job.getOperationId())) {
                generateCodeFile(c.getFileName(), c.getCode());
            }
        }
        log("成功\n");
    }

    private void generateCodeFile(String fileName, String code) throws IOException {
        String path = getJobPath() + fileName;
        final File file = new File(path);
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException("创建目录失败: " + parentFile.getAbsolutePath());
            }
        }
        try (final FileWriter writer = new FileWriter(file, false)) {
            writer.append(code);
            writer.flush();
        }
        chmod(fileName);
    }

    private void chmod(final String fileName) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            return;
        }
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
            if (!parentFile.mkdirs()) {
                throw new IOException("创建目录失败: " + parentFile.getAbsolutePath());
            }
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
        final File yaml = new File(getJobPath() + "/vars.yml");
        final File shell = new File(getJobPath() + "/vars.sh");
        final File parentFile = yaml.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException("创建目录失败: " + parentFile.getAbsolutePath());
            }
        }
        try (final FileWriter w1 = new FileWriter(yaml, true);
             final FileWriter w2 = new FileWriter(shell, true)) {
            final Optional<App> appOp = resources.appDao.findById(job.getAppId());
            if (!appOp.isPresent()) {
                throw new ServiceException("The specified app: "+job.getAppId() + " not found");
            }
            App app = appOp.get();
            w1.append("apptag: ").append(app.getApptag()).append("\n");
            w2.append("#!/bin/bash\n");
            w2.append("export apptag=\"").append(app.getApptag()).append("\"\n");
            for (final AppVariable var : app.getVars()) {
                boolean opVarOverride = false;
                for (final OperationVariable opVar: operationVariables) {
                    if (opVar.getName().equals(var.getName())) {
                        opVarOverride = true;
                        break;
                    }
                }
                if (!var.isDeleted() && !opVarOverride) {
                    w1.append(var.getName()).append(": ").append(var.getValue()).append("\n");
                    w2.append("export ").append(var.getName()).append("=\"").append(var.getValue()).append("\"\n");
                }
            }
            if (job.getVersionId() != null) {
                final Optional<Version> verOp = resources.versionDao.findById(job.getVersionId());
                if (verOp.isPresent()) {
                    Version ver = verOp.get();
                    w1.append("version: ").append(ver.getName()).append("\n");
                    w2.append("export version=\"").append(ver.getName()).append("\"\n");
                    w1.append("version_id: ").append(Long.toString(ver.getId())).append("\n");
                    w2.append("export version_id=\"").append(Long.toString(ver.getId())).append("\"\n");
                    w1.append("exec_opt: ").append(ver.getExecOpt()).append("\n");
                    w2.append("export exec_opt=\"").append(ver.getExecOpt()).append("\"\n");
                    w1.append("repository: ").append(ver.getRepository()).append("\n");
                    w2.append("export repository=\"").append(ver.getRepository()).append("\"\n");
                    w1.append("revision: ").append(ver.getRevision()).append("\n");
                    w2.append("export revision=\"").append(ver.getRevision()).append("\"\n");
                    long buildNo = ver.getBuildNo();
                    if (operation.getType() == AppOperationType.BUILD) {
                        buildNo++;
                    }
                    w1.append("build_no: ").append(Long.toString(buildNo)).append("\n");
                    w2.append("export build_no=\"").append(Long.toString(buildNo)).append("\"\n");
                    for (final VersionVariable var : ver.getVars()) {
                        boolean opVarOverride = false;
                        for (final OperationVariable opVar: operationVariables) {
                            if (opVar.getName().equals(var.getName())) {
                                opVarOverride = true;
                                break;
                            }
                        }
                        if (!var.isDeleted() && !opVarOverride) {
                            w1.append(var.getName()).append(": ").append(var.getValue()).append("\n");
                            w2.append("export ").append(var.getName()).append("=\"").append(var.getValue()).append("\"\n");
                        }
                    }
                    for (final OperationVariable var: operationVariables) {
                        w1.append(var.getName()).append(": ").append(var.getValue()).append("\n");
                        w2.append("export ").append(var.getName()).append("=\"").append(var.getValue()).append("\"\n");
                    }
                    JwtClaimsSet claims = JwtClaimsSet.builder()
                            .claim("verId", ver.getId())
                            .claim("buildNo", buildNo)
                            .build();
                    String userToken = resources.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                    w1.append("user_token: ").append(userToken).append("\n");
                    w2.append("export user_token=\"").append(userToken).append("\"\n");
                }
            }
            w1.append("job_id: ").append(job.getId().toString()).append("\n");
            w2.append("export job_id=\"").append(job.getId().toString()).append("\"\n");
            w1.flush();
            w2.flush();
            chmod("vars.sh");
        }
        log("成功\n");
    }

}
