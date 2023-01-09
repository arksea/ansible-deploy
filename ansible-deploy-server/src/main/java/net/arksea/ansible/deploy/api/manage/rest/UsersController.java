package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;

import net.arksea.ansible.deploy.api.auth.CurrentUser;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.auth.service.UserDetailsImpl;
import net.arksea.ansible.deploy.api.manage.msg.GetUsers;
import net.arksea.ansible.deploy.api.manage.service.UsersService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestException;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @Autowired
    ClientInfoService clientInfoService;

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:查询')")
    @RequestMapping(path="users", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<GetUsers.Response> getUsers(
                @RequestParam int page, @RequestParam int pageSize,
                @RequestParam(required = false) String nameSearch,
                final HttpServletRequest httpRequest) {
        GetUsers.Request msg = new GetUsers.Request(nameSearch, page, pageSize);
        return new RestResult<>(SUCCEED, usersService.findUsers(msg), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:查询')")
    @RequestMapping(path="users/notInGroup/{groupId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<User>> getUsersNotInGroup(@PathVariable("groupId")final long groupId, final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, usersService.getUsersNotInGroup(groupId), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="users/active/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult blockUser(@PathVariable(name="userId") long userId,
                               final HttpServletRequest httpRequest) {
        usersService.blockUser(userId);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="users/blocked/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult deleteUser(@PathVariable(name="userId") long userId,
                              final HttpServletRequest httpRequest) {
        usersService.deleteUser(userId);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="users/blocked/{userId}", params = {"action=unblock"},method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public String unblockUser(@PathVariable(name="userId") long userId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        usersService.unblockUser(userId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:查询')")
    @RequestMapping(path="roles", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<Role>> getRoles(final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, usersService.getRoles(), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="roles/user/{userId}", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<Boolean> updateUserRoles(@PathVariable(name="userId") long userId,
                @RequestBody List<Long> roleIdList, HttpServletRequest httpRequest) {
        usersService.updateUserRoles(userId, roleIdList);
        return new RestResult<>(SUCCEED, true, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="users/{userId}/password", params = {"action=reset"},method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<String> resetUserPassword(@PathVariable(name="userId") long userId,
                              final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, usersService.resetUserPassword(userId), httpRequest);
    }
    //-------------------------------------------------------------------------
    @RequestMapping(path="sys/openRegistry", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Boolean> getOpenRegistry(final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, usersService.getOpenRegister(), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('用户管理:修改')")
    @RequestMapping(path="sys/openRegistry", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public BaseResult setOpenRegistry(@RequestBody boolean status,
                                      @CurrentUser UserDetailsImpl user,
                                      final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(user, httpRequest);
        if (!info.username.equals("admin")) {
            throw new RestException("只有Admin用户可以修改此状态");
        }
        usersService.setOpenRegistry(status);
        return new BaseResult(SUCCEED, httpRequest);
    }
}
