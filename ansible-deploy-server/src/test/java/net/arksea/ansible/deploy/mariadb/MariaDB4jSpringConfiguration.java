/*
 * #%L
 * MariaDB4j
 * %%
 * Copyright (C) 2012 - 2018 Yuexiang Gao
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
