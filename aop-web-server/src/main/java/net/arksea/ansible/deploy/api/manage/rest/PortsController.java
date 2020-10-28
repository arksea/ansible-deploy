package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;

import net.arksea.ansible.deploy.api.manage.entity.Port;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import net.arksea.ansible.deploy.api.manage.service.PortsService;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


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
        PortSection saved;
        if (portSection.getId() == null) {
            saved = portsService.addPortSection(portSection);
        } else {
            saved = portsService.modifyPortSection(portSection);
        }
        return new RestResult<>(SUCCEED, saved, httpRequest);
    }

    @RequiresPermissions("端口管理:修改")
    @RequestMapping(path = "sections/{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public RestResult<Boolean> deletePortSection(@PathVariable("id") long id,
                                                 final HttpServletRequest httpRequest) {
        portsService.deletePortSection(id);
        return new RestResult<>(SUCCEED, true, httpRequest);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "sections", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<PortSection>> getPortSections(final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, portsService.getPortSections(), httpRequest);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "types", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<PortType>> getPortTypes(final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, portsService.getPortTypes(), httpRequest);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "prefix/{prefix}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<List<Port>> searchByPrefix(@PathVariable("prefix") final String prefix,
            @RequestParam(value = "limit",required = false, defaultValue = "10") final int limit,
            final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, portsService.searchByPrefix(prefix,limit), httpRequest);
    }

    @RequiresPermissions("端口管理:查询")
    @RequestMapping(path = "{value}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<List<Port>> getPortByValue(@PathVariable("value") final int value,final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, portsService.getByValue(value), httpRequest);
    }
}
