package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;

import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
import net.arksea.ansible.deploy.api.manage.service.UsersService;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    ClientInfoService clientInfoService;

    @Autowired
    UsersService usersService;

    //-------------------------------------------------------------------------
    @RequiresPermissions("用户管理:查询")
    @RequestMapping(path="user", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<User> getUserByName(@RequestParam("name") String name, HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, usersService.getUserByName(name), httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequestMapping(path="user/groups", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getUserGroups(final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        Iterable<AppGroup> groups = groupsService.getUserGroups(info.userId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, groups, reqid);
    }

    //-------------------------------------------------------------------------
    @RequestMapping(path="user/apps", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<List<App>> getUserApps(HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        List<App> apps = appService.findByUserId(info.userId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, apps, reqid);
    }

    //-------------------------------------------------------------------------
    @RequestMapping(path="user/password", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<Boolean> modifyUserPwd(@RequestBody String pwd, HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        usersService.setUserPassword(info.userId, pwd);
        return new RestResult<>(SUCCEED, true, httpRequest);
    }

}
