package net.arksea.ansible.deploy.api.auth.rest;

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
 * Created by xiaohaixing on 2022/12/21.
 */
@SpringBootTest(properties = "spring.profiles.active=test") //systemProfile使用此参数
@EntityScan(basePackages = {"net.arksea.ansible.deploy"})
@AutoConfigureMockMvc
public class AuthControllerTests {
    @Autowired
    MockMvc mvc;

    @Test
    @WithUserDetails("user")
    public void userRoles() throws Exception {
        this.mvc.perform(get("/api/user/roles")
                        .header("x-requestid", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":0,\"reqid\":\"1\",\"result\":[\"系统信息查询\"]}"));
    }

    @Test
    @WithUserDetails("user")
    public void getUserPermissions() throws Exception {
        this.mvc.perform(get("/api/user/permissions")
                        .header("x-requestid", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"code\": 0,\n" +
                        "    \"reqid\": \"2\",\n" +
                        "    \"result\": [\n" +
                        "        \"主机管理:查询\",\n" +
                        "        \"应用:查询\",\n" +
                        "        \"端口管理:查询\",\n" +
                        "        \"组管理:查询\",\n" +
                        "        \"用户管理:查询\",\n" +
                        "        \"系统:查询\"\n" +
                        "    ]\n" +
                        "}"));

    }

    @Test
    @WithUserDetails("abc")
    public void getAllPermissions() throws Exception {
        this.mvc.perform(get("/api/user/permissions/childs")
                        .header("x-requestid", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"code\": 0,\n" +
                        "    \"reqid\": \"3\",\n" +
                        "    \"result\": {\n" +
                        "        \"系统:管理\": [\n" +
                        "            \"主机管理:查询\",\n" +
                        "            \"用户管理:修改\",\n" +
                        "            \"应用:查询\",\n" +
                        "            \"端口管理:查询\",\n" +
                        "            \"组管理:修改\",\n" +
                        "            \"组管理:查询\",\n" +
                        "            \"用户管理:查询\",\n" +
                        "            \"系统:查询\"\n" +
                        "        ],\n" +
                        "        \"系统:查询\": [\n" +
                        "            \"主机管理:查询\",\n" +
                        "            \"应用:查询\",\n" +
                        "            \"端口管理:查询\",\n" +
                        "            \"组管理:查询\",\n" +
                        "            \"用户管理:查询\"\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}"));
    }
}
