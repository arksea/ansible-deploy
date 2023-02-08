package net.arksea.ansible.deploy.api.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.restapi.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.nio.charset.StandardCharsets;

public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws java.io.IOException {
        String reqid = request.getHeader("restapi-requestid");
        int code;
        if (authException instanceof CredentialsExpiredException) {
            code = ResultCode.PASSWORD_EXPIRED;
        } else {
            code = ResultCode.UNAUTHORIZED;
        }
        ErrorResult erorr = new ErrorResult(code, reqid,  authException.getMessage());
        String body = objectMapper.writeValueAsString(erorr);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
