package net.arksea.ansible.deploy.api.system;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SystemConfig {
    private static final Logger logger = LogManager.getLogger(SystemConfig.class);

    @Autowired
    Environment env;

    Config config;

    public SystemConfig() {
        Config cfg = ConfigFactory.load();
        config = cfg.getConfig("system").withFallback(cfg);
    }

    @Bean(name= "systemProfile")
    public String systemProfile() {
        String active = env.getProperty("spring.profiles.active");
        String systemProfile;
        if ("dev".equals(active)) {
            systemProfile = "dev";
        } else if ("test".equals(active)) {
            systemProfile = "test";
        } else {
            systemProfile = "prod";
        }
        logger.info("spring.profiles.active={}, systemProfile={}", active, systemProfile);
        return systemProfile;
    }

    @Bean(name = "system")
    public ActorSystem createSystem() {
        return ActorSystem.create("system",config.getConfig("system").withFallback(config));
    }

    @Bean(name = "systemBindPort")
    public int bindPort() {
        return config.getInt("akka.remote.netty.tcp.port");
    }
}
