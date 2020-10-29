package net.arksea.ansible.deploy.api.auth.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.service.LoginInfo;
import net.arksea.restapi.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api")
public class LoginController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    private static final Logger logger = LogManager.getLogger(LoginController.class.getName());
    @Autowired
    UserDao userDao;

    @RequestMapping(path="login", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String login(@RequestBody final LoginInfo info,
                                        final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken upt = new UsernamePasswordToken(info.getName(), info.getPassword());
        upt.setRememberMe(true);
        subject.login(upt);
        Long userId = (Long)subject.getPrincipal();
        User user = userDao.findOne(userId);
        subject.getSession().setAttribute("user_id", user.getId());
        subject.getSession().setAttribute("user_name", user.getName());
        long exp = System.currentTimeMillis()+360000*1000L;
        return RestUtils.createResult(ResultCode.SUCCEED, exp, reqid);
    }

    @RequestMapping(path="logout", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String logout(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

}
