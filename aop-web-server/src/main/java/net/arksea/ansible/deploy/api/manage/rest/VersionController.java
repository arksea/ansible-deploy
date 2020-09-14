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


    @RequestMapping(path="versions/{verId}/hosts/{hostId}", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHost(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
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
}
