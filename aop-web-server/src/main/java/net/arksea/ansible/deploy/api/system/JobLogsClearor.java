package net.arksea.ansible.deploy.api.system;

import net.arksea.ansible.deploy.api.operator.service.JobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;

/**
 * Create by xiaohaixing on 2020/11/13
 */
@Component("jobLogsClearor")
public class JobLogsClearor {

    private static final Logger logger = LogManager.getLogger(JobLogsClearor.class);
    @Value("${job.workRoot}")
    private String jobWorkRoot;

    @Value("${job.logExpireDays:30}")
    private long logExpireDays;

    @Autowired
    private JobService jobService;


    public void run() {
        logger.info("运行数据维护定时任务");
        clearLogFiles();
        jobService.clearJobRecords(logExpireDays);
    }

    private void clearLogFiles() {
        long nowEpochDay = System.currentTimeMillis() / 3600_000 / 24;
        File file = new File(this.jobWorkRoot);
        File[] files = file.listFiles();
        if (files == null) {
            logger.warn("日志维护，未找到相关目录：{}", jobWorkRoot);
        } else {
            for (File f : files) {
                if (f.isDirectory()) {
                    LocalDate date = LocalDate.parse(f.getName());
                    long days = nowEpochDay - date.toEpochDay();
                    if (days > logExpireDays) {
                        doDeleteDir(f.toPath());
                    }
                }
            }
        }
    }

    private void doDeleteDir(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            logger.info("删除过期日志目录成功：{}", dir);
        } catch (Exception ex) {
            logger.warn("删除过期日志目录失败：{}", dir, ex);
        }
    }
}
