/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.arksea.ansible.deploy.api.auth.rest;

import net.arksea.ansible.deploy.api.filter.ApiFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(
//        properties = "spring.profiles.active=test", //securityProfile使用此参数
//        controllers = { HelloController.class }
//)
//@Import({SystemConfig.class, SecurityConfig.class})
//@ComponentScan(basePackages = {"net.arksea.ansible.deploy"})

@SpringBootTest(properties = "spring.profiles.active=test") //systemProfile使用此参数
@EntityScan(basePackages = {"net.arksea.ansible.deploy"})
@AutoConfigureMockMvc
public class LoginControllerTests {

    @Autowired
    MockMvc mvc;

    @Test
    void loginWhenAuthenticatedAndLogout() throws Exception {
        MvcResult result = this.mvc.perform(
            post("/api/login")
                .param("remember-me", "on")
                .with(httpBasic("user", "password"))    )
            .andExpect(status().isOk())
            .andReturn();
        Cookie token = result.getResponse().getCookie("remember-me");

        this.mvc.perform(
            get("/api/user/roles")
                .header("x-requestid", "123456")
                .cookie(token)  )
            .andExpect(status().isOk())
            .andExpect(content().json("{\"code\":0,\"reqid\":\"123456\",\"result\":[\"系统信息查询\"]}"));

        this.mvc.perform(
            get("/api/logout")
                .header("Accept","application/json, text/plain, */*")
                .cookie(token)   )
            .andExpect(header().stringValues("Set-Cookie", "remember-me=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().stringValues("Location", "/api/logout/success"));
    }

    @Test
    void rolesWhenUnauthenticatedThen401() throws Exception {
        this.mvc.perform(get("/api/user/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWhenBadCredentialsThen401() throws Exception {
        this.mvc.perform(post("/api/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWhenBadPasswordThen401() throws Exception {
        this.mvc.perform(post("/api/login")
                .param("remember-me", "on")
                .with(httpBasic("user", "badpassword"))    )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutSuccess() throws Exception {
        this.mvc.perform(get("/api/logout/success")
                        .header("x-requestid", "123456")  )
            .andExpect(status().isOk())
            .andExpect(content().json("{\"code\":0,\"reqid\":\"123456\"}"));
    }
}
