package net.arksea.ansible.deploy.api.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xiaohaixing
 */
@Component("apiFilter")
public class ApiFilter implements Filter {
    @Autowired
    private Environment env;

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setHeader("Access-Control-Allow-Headers","Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials","true");
        String profile = env.getProperty("spring.profiles.active");
        if ("product".equals(profile)) {
            response.setHeader("Access-Control-Allow-Methods","GET, PUT");
        } else  { //测试状态下允许所有方法跨域访问，方便用ng server测试
            response.setHeader("Access-Control-Allow-Origin","http://localhost:4200");
            response.setHeader("Access-Control-Allow-Methods","GET, HEAD, POST, PUT, DELETE, PATCH");
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String reqid = req.getHeader("x-requestid");
        if (reqid == null) {
            reqid = UUID.randomUUID().toString();
        }
        req.setAttribute("x-requestid", reqid);
        chain.doFilter(request, resp);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        //do nothing
    }

}
