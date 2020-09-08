package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.manage.service.UsersService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
public class UsersController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    UsersService usersService;

    //-------------------------------------------------------------------------
    @RequiresPermissions("用户管理:查询")
    @RequestMapping(path="users/active", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<User>> getActiveUsers(final HttpServletRequest httpRequest) {
        Iterable<User> users = usersService.getUsers(true);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, users, reqid);
    }

    @RequiresPermissions("用户管理:修改")
    @RequestMapping(path="users/active/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String blockUser(@PathVariable(name="userId") long userId,
                               final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        usersService.blockUser(userId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequiresPermissions("用户管理:查询")
    @RequestMapping(path="users/blocked", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<User>> getBlockedUsers(final HttpServletRequest httpRequest) {
        Iterable<User> users = usersService.getUsers(false);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, users, reqid);
    }

    @RequiresPermissions("用户管理:修改")
    @RequestMapping(path="users/blocked/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteUser(@PathVariable(name="userId") long userId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        //manageService.deleteUser(userId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequiresPermissions("用户管理:修改")
    @RequestMapping(path="users/blocked/{userId}", params = {"action=unblock"},method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public String unblockUser(@PathVariable(name="userId") long userId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        usersService.unblockUser(userId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
