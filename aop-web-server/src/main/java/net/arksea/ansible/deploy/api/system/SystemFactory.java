package net.arksea.ansible.deploy.api.system;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.arksea.ansible.deploy.api.manage.service.ConfigDataInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 *
 * Created by xiaohaixing on 2017/07/22.
 */
@Component
public class SystemFactory {
    private static final Logger logger = LogManager.getLogger(SystemFactory.class);
    @Bean(name = "system")
    public ActorSystem createSystem() {
        Config config = ConfigFactory.load();
        return ActorSystem.create("system",config.getConfig("system").withFallback(config));
    }

    @Autowired
    private Environment env;

    @Bean(name= "systemProfile")
    public String systemProfile() {
        String active = env.getProperty("spring.profiles.active");
        String systemProfile;
        if ("development".equals(active)) {
            systemProfile = "DEV";
        } else if ("functional-test".equals(active) || "unit-test".equals(active)) {
            systemProfile = "QA";
        } else {
            systemProfile = "online";
        }
        logger.info("spring.profiles.active={}, systemProfile={}", active, systemProfile);
        return systemProfile;
    }

    @Autowired
    ConfigDataInitializer initializer;
}
