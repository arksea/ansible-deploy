package net.arksea.ansible.deploy.api.system;

import net.arksea.ansible.deploy.api.auth.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Component
public class ConfigDataInitializer {

    Logger logger = LogManager.getLogger(ConfigDataInitializer.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    AuthService authService;

    @Resource(name="systemProfile")
    String systemProfile;

    JdbcTemplate jdbcTemplate;

    //初始化静态配置表
    @PostConstruct
    public void init() throws Exception {
        jdbcTemplate = new JdbcTemplate(dataSource);
        Integer count = jdbcTemplate.queryForObject("select count(*) from sys_roles", Integer.class);
        if (count == null || count == 0) {
            Connection conn = dataSource.getConnection();
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                init(conn);
                conn.commit();
                authService.reloadAuth();
                logger.info("初始化数据库静态数据成功");
            } catch (Exception ex) {
                conn.rollback();
                logger.warn("初始化数据库静态数据失败", ex);
            } finally {
                conn.setAutoCommit(autoCommit);
                try {
                    conn.close();
                } catch (Exception ex) {
                    logger.error("close Connection failed", ex);
                }
            }
        }
    }

    private void init(Connection conn) throws Exception {
        if ("test".equals(systemProfile)) {
            init(conn, "/config-data-init-test.sql");
        } else {
             init(conn, "/config-data-init.sql");
        }
    }

    private void init(Connection conn, String sqlFile) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(sqlFile)));
        String line = reader.readLine();
        while (line != null) {
            conn.prepareStatement(line).executeUpdate();
            line = reader.readLine();
        }
    }
}
