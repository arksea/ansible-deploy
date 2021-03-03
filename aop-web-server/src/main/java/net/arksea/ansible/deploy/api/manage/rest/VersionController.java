package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;
import net.arksea.ansible.deploy.api.manage.entity.Version;
import net.arksea.ansible.deploy.api.manage.service.VersionService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.ErrorResult;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api/versions")
public class VersionController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    VersionService versionService;

    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{verId}/hosts/{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteHost(@PathVariable("verId") final long verId,
                             @PathVariable("hostId") final long hostId,
                             final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.removeHostFromVersion(verId, hostId);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{verId}/hosts", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String addHosts(@PathVariable("verId") final long verId,
                           @RequestBody final List<Long> hosts,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.addHosts(verId, hosts);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{verId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String delete(@PathVariable("verId") final long verId,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        versionService.deleteById(verId);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    @RequiresPermissions("应用:修改")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String createVersion(@RequestBody final Version version,
                                final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Version created = versionService.saveVersion(version);
        return RestUtils.createResult(SUCCEED, created.getId(), reqid);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{verId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getVersionAppById(@PathVariable("verId") long verId, HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Version ver = versionService.findOne(verId);
        if (ver == null) {
            return new ErrorResult(FAILED, reqid, "应用版本不存在");
        } else {
            return new RestResult<>(SUCCEED, ver, reqid);
        }
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="template/{typeName}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Version> getVersionTemplate(@PathVariable("typeName") String typeName, HttpServletRequest httpRequest) {
        Version ver = versionService.createVersionTemplate(typeName);
        return new RestResult<>(SUCCEED, ver, httpRequest);
    }
}
