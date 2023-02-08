package net.arksea.ansible.deploy.api.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xiaohaixing
 */
@Component("apiFilter")
public class ApiFilter implements Filter {

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String reqid = req.getHeader("x-requestid");
        if (reqid == null) {
            reqid = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        req.setAttribute("restapi-requestid", reqid);
        chain.doFilter(request, resp);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        //do nothing
    }

}
