package net.arksea.ansible.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EntityScan(basePackages = {"net.arksea.ansible.deploy"})
@EnableScheduling
public class AnsibleDeployApplication {
	public static void main(String[] args) {
		SpringApplication.run(AnsibleDeployApplication.class, args);
	}

}
