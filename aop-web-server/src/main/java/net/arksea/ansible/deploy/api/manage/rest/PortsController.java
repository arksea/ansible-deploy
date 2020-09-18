package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import net.arksea.ansible.deploy.api.manage.service.PortsService;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@RestController
@RequestMapping(value = "/api/ports")
public class PortsController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    PortsService portsService;

    @RequiresPermissions("端口管理:修改")
    @RequestMapping(path = "sections", method = RequestMethod.PUT, produces = MEDIA_TYPE)
    public RestResult<PortSection> savePortSection(@RequestBody final PortSection portSection,
                                  final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        PortSection saved;
        if (portSection.getId() == null) {
            saved = portsService.addPortSection(portSection);
        } else {
            saved = portsService.modifyPortSection(portSection);
        }
        return new RestResult<>(ResultCode.SUCCEED, saved, reqid);
    }

    @RequiresPermissions("端口管理:修改")
    @RequestMapping(path = "sections/{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public RestResult<Boolean> deletePortSection(
            @PathVariable("id") long id,
            final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        portsService.deletePortSection(id);
        return new RestResult<>(ResultCode.SUCCEED, true, reqid);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "sections", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<PortSection>> getPortSections(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Iterable<PortSection> sections = portsService.getPortSections();
        return new RestResult<>(0, sections, reqid);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "types", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<PortType>> getPortTypes(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Iterable<PortType> types = portsService.getPortTypes();
        return new RestResult<>(ResultCode.SUCCEED, types, reqid);
    }
}
