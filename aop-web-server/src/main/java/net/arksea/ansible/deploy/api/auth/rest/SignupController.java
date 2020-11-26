package net.arksea.ansible.deploy.api.auth.rest;

import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.auth.service.ISignupService;
import net.arksea.ansible.deploy.api.auth.service.SignupInfo;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

/**
 *
 * Created by xiaohaixing on 2017/10/31.
 */
@RestController
@RequestMapping(value = "/api/signup")
public class SignupController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    private ISignupService signupService;
    @Autowired
    ClientInfoService clientInfoService;

    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Long> signup(@RequestBody final SignupInfo info, final HttpServletRequest httpRequest) {
        signupService.signup(info, false);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken upt = new UsernamePasswordToken(info.getName(), info.getPassword());
        upt.setRememberMe(true);
        subject.login(upt);
        long exp = System.currentTimeMillis()+360000*1000L;
        return new RestResult<>(SUCCEED, exp, httpRequest);
    }

    @RequiresPermissions("用户管理:修改")
    @RequestMapping(path="adminCreate", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<User> adminSignup(@RequestBody final SignupInfo info, final HttpServletRequest httpRequest) {
        User user = signupService.signup(info, true);
        return new RestResult(SUCCEED,  user, httpRequest);
    }
}
