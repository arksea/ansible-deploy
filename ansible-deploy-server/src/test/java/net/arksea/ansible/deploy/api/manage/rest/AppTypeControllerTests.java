package net.arksea.ansible.deploy.api.manage.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by xiaohaixing on 2022/12/23.
 */
@SpringBootTest(properties = "spring.profiles.active=test") //systemProfile使用此参数
@EntityScan(basePackages = {"net.arksea.ansible.deploy"})
@AutoConfigureMockMvc
public class AppTypeControllerTests {
    @Autowired
    MockMvc mvc;

    @Test
    @WithUserDetails("user")
    public void getAppTypes() throws Exception {
        this.mvc.perform(get("/api/appTypes").header("x-requestid", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":0,\"reqid\":\"1\",\"result\":[{\"id\":1,\"name\":\"Tomcat\",\"description\":\"Tomcat8\",\"appVarDefines\":[{\"id\":1,\"appTypeId\":1,\"name\":\"server_port\",\"formLabel\":\"服务管理端口\",\"inputAddon\":\"\",\"defaultValue\":\"\",\"portType\":{\"id\":2,\"name\":\"SERVER\",\"description\":\"服务管理端口\",\"allCount\":0,\"restCount\":0}},{\"id\":2,\"appTypeId\":1,\"name\":\"http_port\",\"formLabel\":\"HTTP端口\",\"inputAddon\":\"\",\"defaultValue\":\"\",\"portType\":{\"id\":1,\"name\":\"HTTP\",\"description\":\"HTTP接口\",\"allCount\":0,\"restCount\":0}}],\"versionVarDefines\":[{\"id\":36,\"appTypeId\":1,\"name\":\"build_dest\",\"formLabel\":\"构建任务结果路径（相对于工作目录）\",\"inputAddon\":\"\",\"defaultValue\":\"publish/online\",\"portType\":null},{\"id\":37,\"appTypeId\":1,\"name\":\"build_task\",\"formLabel\":\"Gradle构建任务名\",\"inputAddon\":\"\",\"defaultValue\":\"publishOnline\",\"portType\":null}]},{\"id\":2,\"name\":\"Java\",\"description\":\"Java\",\"appVarDefines\":[],\"versionVarDefines\":[]}]}"));
    }

    @Test
    @WithUserDetails("user")
    public void findOne() throws Exception {
        this.mvc.perform(get("/api/appTypes/2").header("x-requestid", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":0,\"reqid\":\"2\",\"result\":{\"id\":2,\"name\":\"Java\",\"description\":\"Java\",\"appVarDefines\":[],\"versionVarDefines\":[]}}"));
    }
}
