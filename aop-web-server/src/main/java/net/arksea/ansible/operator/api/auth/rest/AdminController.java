package net.arksea.ansible.operator.api.auth.rest;

import net.arksea.ansible.operator.api.ResultCode;
import net.arksea.ansible.operator.api.auth.dao.UserDao;
import net.arksea.ansible.operator.api.auth.entity.Permission;
import net.arksea.ansible.operator.api.auth.entity.Role;
import net.arksea.ansible.operator.api.auth.entity.User;
import net.arksea.ansible.operator.api.auth.info.ClientInfo;
import net.arksea.ansible.operator.api.auth.info.GetRoleList;
import net.arksea.ansible.operator.api.auth.info.GetUserList;
import net.arksea.ansible.operator.api.auth.info.PermissionInfo;
import net.arksea.ansible.operator.api.auth.service.IAuthService;
import net.arksea.ansible.operator.api.auth.service.IPermissionService;
import net.arksea.ansible.operator.api.auth.service.IRoleService;
import net.arksea.ansible.operator.api.auth.service.IUserService;
import net.arksea.restapi.RestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    private static final Logger logger = LogManager.getLogger(AdminController.class.getName());

    @Autowired
    UserDao userDao;
    @Autowired
    IUserService iUserService;
    @Autowired
    IRoleService iRoleService;
    @Autowired
    IPermissionService permissionService;
    @Autowired
    IAuthService iAuthService;


    /**
     * 获取后台用户列表
     *
     * @param page
     * @param pageSize
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:用户:查询")
    @RequestMapping(path = "/user/list", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getUserList(@RequestParam int page, @RequestParam int pageSize, final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        GetUserList.Response response = iUserService.getUserList(page, pageSize);
        return RestUtils.createResult(ResultCode.SUCCEED, response, reqid);
    }

    /**
     * 修改分配给用户的角色列表。
     *
     * @param roleIds
     * @param userId
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:用户:管理")
    @RequestMapping(path = "/user/roles", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveUserRole(@RequestBody Long[] roleIds, @RequestParam(name = "userId") Long userId
            , final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        ClientInfo clientInfo = getClientInfo(httpRequest);
        Pair<String, User> result = iUserService.saveUserRole(clientInfo, userId, roleIds);
        if (StringUtils.isEmpty(result.getLeft())) {
            return RestUtils.createResult(ResultCode.SUCCEED, result.getValue().getId(), reqid);
        } else {
            return RestUtils.createError(ResultCode.FAILED, result.getLeft(), reqid);
        }
    }


    /**
     * 获取角色列表
     *
     * @param page
     * @param pageSize
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:角色:查询")
    @RequestMapping(path = "/role/list", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getRoleList(@RequestParam int page, @RequestParam int pageSize, final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        GetRoleList.Response response = iRoleService.getRoleList(page, pageSize);
        return RestUtils.createResult(ResultCode.SUCCEED, response, reqid);
    }

    /**
     * 新增或编辑角色
     *
     * @param roleBody
     * @param id
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:角色:管理")
    @RequestMapping(path = "/role/info", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveRoleInfo(@RequestBody RoleBody roleBody, @RequestParam(name = "id", required = false) Long id
            , final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");

        ClientInfo clientInfo = getClientInfo(httpRequest);
        Pair<String, Role> result = iRoleService.saveRole(clientInfo, id, roleBody.getRole(), roleBody.getDescription());
        if (StringUtils.isEmpty(result.getLeft())) {
            return RestUtils.createResult(ResultCode.SUCCEED, result.getValue().getId(), reqid);
        } else {
            return RestUtils.createError(ResultCode.FAILED, result.getLeft(), reqid);
        }
    }

    /**
     * 修改分配给角色的权限节点。
     *
     * @param permissionIds
     * @param roleId
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:角色:管理")
    @RequestMapping(path = "/role/permissions", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveRolePermission(@RequestBody Long[] permissionIds, @RequestParam(name = "roleId") Long roleId
            , final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        ClientInfo clientInfo = getClientInfo(httpRequest);
        Pair<String, Role> result = iRoleService.saveRolePermission(clientInfo, roleId, permissionIds);
        if (StringUtils.isEmpty(result.getLeft())) {
            //编辑成功时，重新加载权限数据
            iAuthService.reloadAuth();
            return RestUtils.createResult(ResultCode.SUCCEED, result.getValue().getId(), reqid);
        } else {
            return RestUtils.createError(ResultCode.FAILED, result.getLeft(), reqid);
        }
    }

    /**
     * 获取当前数据库中的全部权限项
     *
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:权限:查询")
    @RequestMapping(path = "/permission/list", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public String getPermissionList(final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        List<PermissionInfo> list = permissionService.getPermissionList();
        return RestUtils.createResult(ResultCode.SUCCEED, list, reqid);
    }

    /**
     * 新增或编辑权限项
     *
     * @param permissionBody
     * @param id
     * @param httpRequest
     * @return
     */
    @RequiresPermissions("系统管理:权限:管理")
    @RequestMapping(path = "/permission/info", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String savePermissionInfo(@RequestBody PermissionBody permissionBody
            , @RequestParam(name = "id", required = false) Long id
            , final HttpServletRequest httpRequest) {
        String reqid = (String) httpRequest.getAttribute("restapi-requestid");
        PermissionInfo permissionInfo = new PermissionInfo(0L, permissionBody.permission, permissionBody.description, 1, permissionBody.pid, 1);
        ClientInfo clientInfo = getClientInfo(httpRequest);
        Pair<String, Permission> result = permissionService.savePermission(clientInfo, id, permissionInfo);
        if (StringUtils.isEmpty(result.getLeft())) {
            //编辑成功时，重新加载权限数据
            iAuthService.reloadAuth();
            return RestUtils.createResult(ResultCode.SUCCEED, result.getValue().getId(), reqid);
        } else {
            return RestUtils.createError(ResultCode.FAILED, result.getLeft(), reqid);
        }
    }

    private ClientInfo getClientInfo(HttpServletRequest httpRequest) {
        Subject subject = SecurityUtils.getSubject();
        Long userId = (Long)subject.getSession().getAttribute("user_id");
        String userName = (String)subject.getSession().getAttribute("user_name");
        if (userId == null || userName == null) {
            //session失效则根据remberMe记录的用户Id重新设置session
            userId = (long)subject.getPrincipal();
            logger.debug("session失效，重新加载，userID={}", userId);
            User user = userDao.findOne(userId);
            if (user == null) {
                throw new UnauthenticatedException("获取用户信息失败，userID=" + userId);
            } else {
                userName = user.getName();
                subject.getSession().setAttribute("user_id", userId);
                subject.getSession().setAttribute("user_name", userName);
            }
        }
        final String remoteIp = httpRequest.getRemoteAddr();
        return new ClientInfo(userId, userName, remoteIp);
    }

    public static class PermissionBody {
        private String permission;             //
        private String description;         //
        private Long pid;          //

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getPid() {
            return pid;
        }

        public void setPid(Long pid) {
            this.pid = pid;
        }
    }

    public static class RoleBody {
        private String role;
        private String description;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
