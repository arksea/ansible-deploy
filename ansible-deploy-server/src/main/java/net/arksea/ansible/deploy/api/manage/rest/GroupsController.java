package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.GroupsService;
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
@RequestMapping(value = "/api/groups")
public class GroupsController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    GroupsService groupsService;

    @Autowired
    ClientInfoService clientInfoService;

    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String createGroup(@RequestParam final String name,
                              @RequestParam final String desc,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppGroup group = groupsService.createGroup(name, desc);
        return RestUtils.createResult(SUCCEED, group.getId(), reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public String createGroup(@PathVariable("groupId") final Long groupId,
                              @RequestParam final String name,
                              @RequestParam final String desc,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        int count = groupsService.modifyGroup(groupId, name, desc);
        return RestUtils.createResult(SUCCEED, count, reqid);
    }
    //-------------------------------------------------------------------------
   @PreAuthorize("hasAuthority('组管理:查询')")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getAppGroups(final HttpServletRequest httpRequest,
        @RequestParam(name="countStat", required = false, defaultValue = "false") boolean countStat) {
        Iterable<AppGroup> groups = countStat?groupsService.getAppGroupsAndStat():groupsService.getAppGroups();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, groups, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:查询')")
    @RequestMapping(path="{groupId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<AppGroup> getAppGroup(@PathVariable(name="groupId") long groupId,
                                             final HttpServletRequest httpRequest) {
        AppGroup group = groupsService.getAppGroupById(groupId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, group, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteGroup(@PathVariable(name="groupId") long groupId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.deleteAppGroup(groupId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/hosts/{hostId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHost(@PathVariable(name="groupId") long groupId,
                          @PathVariable(name="hostId") long hostId,
                          final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.addHost(groupId, hostId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/hosts/{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String removeHost(@PathVariable(name="groupId") long groupId,
                          @PathVariable(name="hostId") long hostId,
                          final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.removeHost(groupId, hostId);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/users/{userId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addMember(@PathVariable(name="groupId") long groupId,
                          @PathVariable(name="userId") long userId,
                          final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.addMember(groupId, userId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/users/{userId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String removeMember(@PathVariable(name="groupId") long groupId,
                             @PathVariable(name="userId") long userId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.removeMember(groupId, userId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/apps/{appId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addApp(@PathVariable(name="groupId") long groupId,
                          @PathVariable(name="appId") long appId,
                          final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.addApp(groupId, appId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="{groupId}/apps/{appId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String removeApp(@PathVariable(name="groupId") long groupId,
                             @PathVariable(name="appId") long appId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        groupsService.removeApp(groupId, appId);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:查询')")
    @RequestMapping(path="user/{userId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getUserGroups(@PathVariable(name="userId") long userId, HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, groupsService.getUserGroups(userId), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('组管理:修改')")
    @RequestMapping(path="user/{userId}", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<Boolean> updateUserGroups(@PathVariable(name="userId") long userId,
                                               @RequestBody List<Long> groupIdList, HttpServletRequest httpRequest) {
        groupsService.updateUserGroups(userId, groupIdList);
        return new RestResult<>(SUCCEED, true, httpRequest);
    }
}
