package net.arksea.ansible.deploy.api.operator.service;

import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Set;


/**
 *
 * @author xiaohaixing
 */
public class JobContextCreator {

    private Logger logger = LogManager.getLogger(JobContextCreator.class);
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
        AppOperation op = resources.appOperationDao.findOne(job.getOperationId());
        for (AppOperationCode c : op.getCodes()) {
            generateCodeFile(c);
        }
    }
    private void generateCodeFile(AppOperationCode code) throws IOException {
        log("生成脚本文件'"+code.getFileName()+"'...");
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
        log("成功\n");
        chmod(code.getFileName());
    }

    protected void chmod(final String fileName) throws IOException {
        String cmd = "chmod u+x " + getJobPath() + fileName;
        final Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            log("修改文件权限'"+fileName+"'...");
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
            log("成功\n");
        } finally {
            process.destroy();
        }
    }

//    public void initContext(final String path) throws IOException {
//
//
//        final Set<Version> targetVersions = new HashSet<>();
//        for (final Host host : hosts) {
//            for (final Version v : app.getVersions()) {
//                final Set<Host> targetHosts = v.getTargetHosts();
//                final Version t_Version = new Version();
//                t_Version.setExecOpt(v.getExecOpt());
//                t_Version.setRepository(v.getRepository());
//                t_Version.setName(v.getName());
//                t_Version.setRevision(v.getRevision());
//                Set<Host> versionHosts = new HashSet<>();
//                for (final Host versionTargetHost : targetHosts) {
//                    if (host.getId().equals(versionTargetHost.getId())) {
//                        versionHosts.add(versionTargetHost);
//                    }
//                }
//                if (!versionHosts.isEmpty()) {
//                    t_Version.setTargetHosts(versionHosts);
//                    targetVersions.add(t_Version);
//                }
//            }
//        }
//        generateVarFile(app, path);
//        generateDeployFile(targetVersions, path);
//    }

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
        try (final FileWriter writer = new FileWriter(file)) {
            writer.append("[deploy_target]\n");
            for (final Long hid : hosts) {
                Host h = resources.hostDao.findOne(hid);
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
//            writer.append("deploy_path: ");
//            writer.append(app.getDeployPath());
//            writer.append("\n");
            for (final AppVariable var : app.getVars()) {
                writer.append(var.getName());
                writer.append(": ");
                writer.append(var.getValue());
                writer.append("\n");
            }
            writer.flush();
        }
        log("成功\n");
    }

//    private void generateSetenv(final Set<Version> versions, final String path) throws IOException {
//        final String pathname = CMD_PATH + app.getAppType().getName() + "/templates/setenv.sh";
//        final File file = new File(pathname);
//        final BufferedReader br = new BufferedReader(new FileReader(file));
//        final List<String> lists = new ArrayList<String>();
//        String str;
//        while ((str = br.readLine()) != null) {
//            lists.add(str);
//        }
//        br.close();
//
//        for (final Version version : versions) {
//            for (final Host h : version.getTargetHosts()) {
//                File setenv_file = new File(path + "/setenv_" + h.getPrivateIp() + ".sh");
//                BufferedWriter bw = new BufferedWriter(new FileWriter(setenv_file));
//
//                for (String strr : lists) {
//                    if (strr.contains("{{EXEC_OPTS}}")) {
//                        strr = strr.replaceAll("\\{\\{EXEC_OPTS\\}\\}", version.getExecOpt());
//                    }
//                    bw.write(strr);
//                    bw.newLine();
//                }
//                bw.flush();
//                bw.close();
//            }
//        }
//    }

//    private void generateDeployFile(final Set<Version> targetVersions, final String path) throws IOException {
//        for (final Version version : targetVersions) {
//            for (final Host host : version.getTargetHosts()) {
//                final BufferedWriter bw = new BufferedWriter(
//                        new FileWriter(new File(path + "/deploy_" + host.getPrivateIp() + ".yml")));
//
//                bw.write("- name: Copy the code from repository");
//                bw.newLine();
//                bw.write("  subversion: repo=svn://");
//                //bw.write(paramConfig.getSvnprivateaddr());
//                bw.write("/");
//                bw.write(app.getApptag());
//                bw.write("/");
//                bw.write(version.getRepository());
//                bw.write(" dest=/home/");
//                bw.write(app.getApptag());
//                if ("Tomcat".equalsIgnoreCase(app.getAppType().getName())) {
//                    bw.write("/tomcat/webapps/");
//                    bw.write(app.getDeployPath());
//                } else {
//                    bw.write("/");
//                    bw.write(app.getDeployPath());
//                }
//                bw.write(" force=no username=deploy password=unicorn4Felink");
//                bw.write(" revision=");
//                bw.write(version.getRevision());
//                bw.newLine();
//                bw.write("  become: yes");
//                bw.newLine();
//                bw.write("  become_user: ");
//                bw.write("\"" + app.getApptag() + "\"");
//
//                if ("deploy".equalsIgnoreCase(operation) && ("JavaServer".equalsIgnoreCase(app.getAppType().getName())
//                        || "Command".equalsIgnoreCase(app.getAppType().getName()))) {
//                    bw.newLine();
//                    bw.write("  notify: restart app");
//                }
//                bw.flush();
//                bw.close();
//            }
//        }
//    }

//    public void onSucceed(final String ret) {
//        if (operation.equals(TYPE_STATUS)) {
//            final Map<String, String> status = OperationUtil.parseStatusToJson(ret);
//            final OpViewMessage msg = new OpViewMessage(TYPE_STATUS, status);
//            final Map<WebSocketSession, IOperationViewWriter> writers = OperationWebSocketHandler
//                    .getViewWriters(app.getId());
//            for (final IOperationViewWriter writer : writers.values()) {
//                writer.write(msg);
//            }
//        }
//    }

//    public void onDeleteSucceed(final String ret) {
//        if (operation.equals(TYPE_DELETE)) {
//            final Map<WebSocketSession, IOperationViewWriter> writers = OperationWebSocketHandler
//                    .getViewWriters(app.getId());
//            String value = "";
//            if (StringUtils.isNotBlank(ret) && ret.contains("fatal")) {
//                value = "error";
//            } else {
//                final StringBuffer sb = new StringBuffer();
//                final Iterator<ManagedHost> iterator = hosts.iterator();
//                while (iterator.hasNext()) {
//                    sb.append(iterator.next().getId());
//                    sb.append(":");
//                }
//
//                if (sb.length() != 0) {
//                    value = sb.substring(0, sb.length() - 1);
//                }
//            }
//
//            final OpViewMessage successMsg = new OpViewMessage(TYPE_DELETE, value);
//            for (final IOperationViewWriter writer : writers.values()) {
//                writer.write(successMsg);
//            }
//        }
//    }


}
