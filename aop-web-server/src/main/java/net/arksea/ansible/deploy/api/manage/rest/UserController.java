package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;

import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.msg.GetUserApps;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
import net.arksea.ansible.deploy.api.manage.service.UsersService;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public RestResult<GetUserApps.Response> getUserApps(
                @RequestParam int page, @RequestParam int pageSize,
                @RequestParam(required = false) String nameSearch,
                HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        GetUserApps.Request msg = new GetUserApps.Request(info.userId, nameSearch, page, pageSize);
        return new RestResult<>(SUCCEED, appService.findUserApps(msg), httpRequest);
    }

    //-------------------------------------------------------------------------
    public static class ModifyPassword {
        public String oldPassword;
        public String newPassword;
    }
    @RequestMapping(path="user/password", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<Boolean> modifyUserPwd(@RequestBody ModifyPassword pwd, HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        usersService.modifyUserPassword(info.userId, pwd.oldPassword, pwd.newPassword);
        return new RestResult<>(SUCCEED, true, httpRequest);
    }

}
