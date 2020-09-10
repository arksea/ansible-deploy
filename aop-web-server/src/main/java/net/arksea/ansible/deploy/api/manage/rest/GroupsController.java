package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
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
public class GroupsController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    GroupsService groupsService;

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="groups", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String createGroup(@RequestParam final String name,
                              @RequestParam final String desc,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppGroup group = groupsService.createGroup(name, desc);
        return RestUtils.createResult(ResultCode.SUCCEED, group.getId(), reqid);
    }

    @RequiresPermissions("组管理:查询")
    @RequestMapping(path="groups", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getAppGroups(final HttpServletRequest httpRequest) {
        Iterable<AppGroup> groups = groupsService.getAppGroups();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, groups, reqid);
    }

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="groups/{groupId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteGroup(@PathVariable(name="groupId") long groupId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.deleteAppGroup(groupId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="groups/{groupId}/hosts/{hostId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHost(@PathVariable(name="groupId") long groupId,
                          @PathVariable(name="hostId") long hostId,
                          final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.addHost(groupId, hostId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
