package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.service.ManageService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api")
public class ManageController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    ManageService manageService;

    @RequestMapping(path="groups", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String createGroup(@RequestParam final String name,
                              @RequestParam final String desc,
                              final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppGroup group = manageService.createGroup(name, desc);
        return RestUtils.createResult(ResultCode.SUCCEED, group.getId(), reqid);
    }

    @RequestMapping(path="groups", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppGroup>> getAppGroups(final HttpServletRequest httpRequest) {
        Iterable<AppGroup> groups = manageService.getAppGroups();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, groups, reqid);
    }
}
