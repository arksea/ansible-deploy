package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.ansible.deploy.api.manage.service.HostsService;
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
public class HostsController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    HostsService hostsService;

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="hosts", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveHost(@RequestBody final Host host,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Host saved = hostsService.saveHost(host);
        return RestUtils.createResult(ResultCode.SUCCEED, saved.getId(), reqid);
    }

    @RequiresPermissions("组管理:查询")
    @RequestMapping(path="hosts", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<Host>> getAppGroups(final HttpServletRequest httpRequest) {
        Iterable<Host> hosts = hostsService.getHosts();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, hosts, reqid);
    }

    @RequiresPermissions("组管理:修改")
    @RequestMapping(path="hosts/{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteHost(@PathVariable(name="hostId") long hostId,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        hostsService.deleteHost(hostId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
