package net.arksea.ansible.deploy.api.auth.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.auth.service.IAuthService;
import net.arksea.restapi.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/5/15
 */
@RestController
@RequestMapping(value = "/api/user")
public class AuthController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    IAuthService authService;

    @Autowired
    ClientInfoService clientInfoService;

    @RequestMapping(path="permissions", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getUserPermissions(final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Set<String> perms = authService.getPermissionsByUserName(info.username);
        return RestUtils.createResult(ResultCode.SUCCEED, perms, reqid);
    }

    @RequestMapping(path="roles", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getUserRoles(final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Set<String> roles = authService.getRolesByUserName(info.username);
        return RestUtils.createResult(ResultCode.SUCCEED, roles, reqid);
    }

    @RequestMapping(path="permissions/childs", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getAllPermissionsChilds(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Map<String, Set<String>> map = authService.getAllPermissionsChilds();
        return RestUtils.createResult(ResultCode.SUCCEED, map, reqid);
    }
}
