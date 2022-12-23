package net.arksea.ansible.deploy.mariadb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class MariaDB4jSpringConfiguration {
    private final static Logger logger = LogManager.getLogger(MariaDB4jSpringConfiguration.class);

    //定义为静态是为了防止MariaDB4jSpringService被多次实例化
    //JUnit会合并多个Context配置相同的测试，当测试集包含Context配置不同的测试时就会有多次实例化的问题
    private final static MariaDB4jSpringService mariaDB4jSpringService;
    static {
        logger.info("========new MariaDB4jSpringService()========");
        mariaDB4jSpringService = new MariaDB4jSpringService();
    }

    @Bean
    public MariaDB4jSpringService mariaDB4j() {
        return mariaDB4jSpringService;
    }
}
