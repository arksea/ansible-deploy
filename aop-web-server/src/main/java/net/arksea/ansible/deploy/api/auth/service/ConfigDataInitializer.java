package net.arksea.ansible.deploy.api.auth.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Component
public class ConfigDataInitializer {

    Logger logger = LogManager.getLogger(ConfigDataInitializer.class);

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbcTemplate;

    //初始化静态配置表
    @PostConstruct
    public void init() throws SQLException {
        jdbcTemplate = new JdbcTemplate(dataSource);
        int count = jdbcTemplate.queryForObject("select count(*) from sys_roles", Integer.class);
        if (count == 0) {
            Connection conn = dataSource.getConnection();
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                initRoles(conn);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
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

    private void initRoles(Connection conn) throws SQLException {
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (1, b'1', '系统信息增、删、改：用户，群组，主机', '系统管理员')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (2, b'1', '系统信息查询：用户、群组、主机', '系统信息查询')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (3, b'1', '应用部署', '应用部署')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (4, b'1', '端口管理', '端口管理')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (5, b'1', '添加应用类型，编辑应用操作脚本', '操作管理')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (6, b'1', '查看操作脚本', '操作查询')").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles` (`id`, `available`, `description`, `role`) VALUES (7, b'1', '应用增、删、改', '应用管理')").executeUpdate();

        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (1, b'1', '组信息管理', '组管理:修改', 902)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (2, b'1', '组信息查询', '组管理:查询', 901)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (3, b'1', '用户信息管理', '用户管理:修改', 902)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (4, b'1', '用户信息查询', '用户管理:查询', 901)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (5, b'1', '应用管理', '应用:修改', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (6, b'1', '执行应用的操作脚本', '应用:操作', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (7, b'1', '应用信息查询', '应用:查询', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (8, b'1', '端口管理', '端口管理:修改', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (9, b'1', '端口查询', '端口管理:查询', 901)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (10, b'1', '增删改操作信息', '操作管理:修改', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (11, b'1', '查询操作脚本', '操作管理:查询', NULL)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (901, b'1', '查询系统信息', '系统:查询', 902)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_permissions` (`id`, `available`, `description`, `permission`, `pid`) VALUES (902, b'1', '增删改系统信息', '系统:管理', NULL)").executeUpdate();

        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (1, 902)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (2, 901)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (3, 6)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (3, 7)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (4, 8)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (4, 9)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (5, 10)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (5, 11)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (6, 11)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (7, 5)").executeUpdate();
        conn.prepareStatement("INSERT INTO `sys_roles_permissions` (`role_id`, `permission_id`) VALUES (7, 7)").executeUpdate();
    }
}
