package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.UserService;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api")
public class UserController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    GroupsService groupsService;

    @Autowired
    AppService appService;

    @Autowired
    UserService userService;

    @RequiresPermissions("应用:查询")
    @RequestMapping(path="user/groups", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getUserGroups(final HttpServletRequest httpRequest) {
        ClientInfo info = userService.getClientInfo(httpRequest);
        Iterable<AppGroup> groups = groupsService.getUserGroups(info.userId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, groups, reqid);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="user/apps", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<List<App>> getUserApps(HttpServletRequest httpRequest) {
        ClientInfo info = userService.getClientInfo(httpRequest);
        List<App> apps = appService.findByUserId(info.userId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, apps, reqid);
    }
}
