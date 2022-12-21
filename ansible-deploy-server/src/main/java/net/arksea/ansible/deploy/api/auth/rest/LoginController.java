package net.arksea.ansible.deploy.api.auth.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.TWO_WEEKS_S;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api")
public class LoginController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @RequestMapping(path="login", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public ResponseEntity<RestResult<Long>> login(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        long exp = System.currentTimeMillis()+TWO_WEEKS_S*1000L;
        RestResult<Long> result = new RestResult<>(ResultCode.SUCCEED, exp, reqid);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path="logout/success", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String logoutSuccess(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
