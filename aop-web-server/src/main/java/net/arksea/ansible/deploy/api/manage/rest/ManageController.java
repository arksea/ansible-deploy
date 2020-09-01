package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.ManageService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
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
public class ManageController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    ManageService manageService;

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="groups", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String createGroup(@RequestParam final String name,
                              @RequestParam final String desc,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppGroup group = manageService.createGroup(name, desc);
        return RestUtils.createResult(ResultCode.SUCCEED, group.getId(), reqid);
    }

    @RequiresPermissions("组管理:查询")
    @RequestMapping(path="groups", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getAppGroups(final HttpServletRequest httpRequest) {
        Iterable<AppGroup> groups = manageService.getAppGroups();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, groups, reqid);
    }

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="groups/{groupId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteGroup(@PathVariable(name="groupId") long groupId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        manageService.deleteAppGroup(groupId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("用户管理:查询")
    @RequestMapping(path="users/active", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<User>> getActiveUsers(final HttpServletRequest httpRequest) {
        Iterable<User> users = manageService.getUsers(true);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, users, reqid);
    }

    @RequiresPermissions("用户管理:修改")
    @RequestMapping(path="users/active/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String blockUser(@PathVariable(name="userId") long userId,
                               final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        manageService.blockUser(userId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequiresPermissions("用户管理:查询")
    @RequestMapping(path="users/blocked", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<User>> getBlockedUsers(final HttpServletRequest httpRequest) {
        Iterable<User> users = manageService.getUsers(false);
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
        manageService.unblockUser(userId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
