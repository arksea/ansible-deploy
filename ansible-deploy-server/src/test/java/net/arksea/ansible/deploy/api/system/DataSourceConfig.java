package net.arksea.ansible.deploy.api.system;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

//@Configuration
//@Profile("test")
public class DataSourceConfig {

    @Value("${spring.datasource.name}")
    String databaseName;
    @Value("${spring.datasource.username}")
    String userName;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${spring.datasource.driverClassName}")
    String driver;

    @Bean
    public MariaDB4jSpringService mariaDB4jSpringService() {
        return new MariaDB4jSpringService();
    }

    @Bean
    public DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService) throws ManagedProcessException {

        mariaDB4jSpringService.getDB().createDB(databaseName);
        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();
        DataSource dataSource = DataSourceBuilder.create()
                .username(userName)
                .password(password)
                .url(config.getURL(databaseName))
                .driverClassName(driver)
                .build();

        // 配置Flyway
//        FluentConfiguration fluentConfiguration = new FluentConfiguration().dataSource(dataSource).locations("filesystem:src/main/resources/db/migration");
//        Flyway flyway = new Flyway(fluentConfiguration);
//        flyway.migrate(); #datasource初始化时会自动migration所有的schema

        return dataSource;
    }
}
