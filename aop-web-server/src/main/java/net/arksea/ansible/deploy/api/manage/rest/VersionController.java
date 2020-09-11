package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.service.UserService;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VersionController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    GroupsService groupsService;

    @Autowired
    AppService appService;

    @Autowired
    UserService userService;


    @RequestMapping(path="versions/{verId}/hosts/{hostId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHost(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
