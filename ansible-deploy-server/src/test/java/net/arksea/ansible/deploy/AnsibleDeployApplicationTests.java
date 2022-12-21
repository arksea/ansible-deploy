package net.arksea.ansible.deploy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test") //systemProfile使用此参数
@EntityScan(basePackages = {"net.arksea.ansible.deploy"})
class AnsibleDeployApplicationTests {

	@Test
	void contextLoads() {
	}

}
