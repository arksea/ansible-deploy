package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import net.arksea.ansible.deploy.api.manage.service.PortsService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(path = "sections", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String savePortSection(@RequestBody final PortSection portSection,
                                  final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        PortSection saved = portsService.addPortSection(portSection);
        return RestUtils.createResult(ResultCode.SUCCEED, saved.getId(), reqid);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "sections", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<PortType>> getPortTypes(final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Iterable<PortType> types = portsService.getPortTypes();
        return new RestResult<>(0,types, reqid);
    }

}
