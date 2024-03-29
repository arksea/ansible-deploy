package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.ansible.deploy.api.manage.msg.GetHosts;
import net.arksea.ansible.deploy.api.manage.service.HostsService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api/hosts")
public class HostsController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    HostsService hostsService;

    @PreAuthorize("hasAuthority('主机管理:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveHost(@RequestBody final Host host,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Host saved = hostsService.saveHost(host);
        return RestUtils.createResult(SUCCEED, saved, reqid);
    }

    @PreAuthorize("hasAuthority('主机管理:修改')")
    @RequestMapping(path="batch", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Iterable<Host>> saveHosts(
                           @RequestParam String ipRange,
                           @RequestParam String descPrefix,
                           @RequestParam long groupId,
                           final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, hostsService.batchAddHosts(ipRange,descPrefix, groupId), httpRequest);
    }

    @PreAuthorize("hasAuthority('主机管理:查询')")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<GetHosts.Response> getHosts(
            @RequestParam int page, @RequestParam int pageSize,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String ipSearch,
            final HttpServletRequest httpRequest) {
        GetHosts.Request msg = new GetHosts.Request(groupId, ipSearch, page, pageSize);
        return new RestResult<>(SUCCEED, hostsService.findHosts(msg), httpRequest);
    }

    @PreAuthorize("hasAuthority('主机管理:查询')")
    @RequestMapping(path="notInGroup", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<Host>> getHostsNotInGroup(final HttpServletRequest httpRequest) {
        Iterable<Host> hosts = hostsService.getNotInGroup();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, hosts, reqid);
    }

    @PreAuthorize("hasAuthority('主机管理:修改')")
    @RequestMapping(path="{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteHost(@PathVariable(name="hostId") long hostId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        hostsService.deleteHost(hostId);
        return RestUtils.createResult(SUCCEED, reqid);
    }
}
