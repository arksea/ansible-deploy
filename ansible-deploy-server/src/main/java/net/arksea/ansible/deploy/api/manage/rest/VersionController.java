package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.manage.entity.Version;
import net.arksea.ansible.deploy.api.manage.service.VersionService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.arksea.ansible.deploy.api.ResultCode.FAILED;
import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

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

    @Autowired
    JwtDecoder jwtDecoder;

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="{verId}/hosts/{hostId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult deleteHost(@PathVariable("verId") final long verId,
                             @PathVariable("hostId") final long hostId,
                             final HttpServletRequest httpRequest) {
        versionService.removeHostFromVersion(verId, hostId);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="{verId}/hosts", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public BaseResult addHosts(@PathVariable("verId") final long verId,
                           @RequestBody final List<Long> hosts,
                           final HttpServletRequest httpRequest) {
        versionService.addHosts(verId, hosts);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="{verId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult delete(@PathVariable("verId") final long verId,
                           final HttpServletRequest httpRequest) {
        versionService.deleteById(verId);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Long> createVersion(@RequestBody final Version version, final HttpServletRequest httpRequest) {
        Version created = versionService.saveVersion(version);
        return new RestResult<>(SUCCEED, created.getId(), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:查询')")
    @RequestMapping(path="{verId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getVersionAppById(@PathVariable("verId") long verId, HttpServletRequest httpRequest) {
        Version ver = versionService.findOne(verId);
        return new RestResult<>(SUCCEED, ver, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="template/{typeName}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Version> getVersionTemplate(@PathVariable("typeName") String typeName, HttpServletRequest httpRequest) {
        Version ver = versionService.createVersionTemplate(typeName);
        return new RestResult<>(SUCCEED, ver, httpRequest);
    }
    //-------------------------------------------------------------------------
    @RequestMapping(path="{verId}/buildno", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public BaseResult setVersionBuildNo(@PathVariable("verId") long versionId,
                                @RequestParam("token") String token,
                                @RequestParam("buildno") long buildNo,
                                final HttpServletRequest httpRequest) {
        Jwt jwt = jwtDecoder.decode(token);
        if ((Long)jwt.getClaim("verId") == versionId && (Long)jwt.getClaim("buildNo") == buildNo) {
            versionService.setVersionBuildNo(versionId, buildNo);
            return new BaseResult(SUCCEED, httpRequest);
        } else {
            return new BaseResult(FAILED, httpRequest);
        }
    }
    //-------------------------------------------------------------------------
    @RequestMapping(path="{verId}/deploy", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public BaseResult setVersionDeployedNo(@PathVariable("verId") long versionId,
                                        @RequestParam("token") String token,
                                        @RequestParam("buildno") long buildNo,
                                        final HttpServletRequest httpRequest) {
        Jwt jwt = jwtDecoder.decode(token);

        if ((Long)jwt.getClaim("verId") == versionId && (Long)jwt.getClaim("buildNo") == buildNo) {
            versionService.setVersionDeployedNo(versionId, buildNo);
            return new BaseResult(SUCCEED, httpRequest);
        } else {
            return new BaseResult(FAILED, httpRequest);
        }
    }
}
