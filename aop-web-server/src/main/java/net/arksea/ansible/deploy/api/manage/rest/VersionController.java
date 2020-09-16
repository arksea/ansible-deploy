package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.service.VersionService;
import net.arksea.restapi.RestUtils;
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
public class VersionController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    VersionService versionService;

    @RequestMapping(path="versions/{verId}/hosts/{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteHost(@PathVariable("verId") final long verId,
                             @PathVariable("hostId") final long hostId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.removeHostFromVersion(verId, hostId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequestMapping(path="versions/{verId}/hosts", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHosts(@PathVariable("verId") final long verId,
                           @RequestBody final List<Long> hosts,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.addHosts(verId, hosts);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequestMapping(path="versions/{verId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String delete(@PathVariable("verId") final long verId,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.deleteById(verId);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }
}
