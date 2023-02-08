package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.manage.entity.AppCustomOperationCode;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

/**
 * Create by xiaohaixing on 2020/8/28
 */
@RestController
@RequestMapping(value = "/api/appCodes")
public class AppCodesController {

    @Autowired
    AppService appService;

    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteAppCode(@PathVariable(name = "id") final Long id, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        appService.deleteAppCode(id);
        return RestUtils.createResult(SUCCEED, "", reqid);
    }

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveAppCodes(@RequestBody List<AppCustomOperationCode> codes, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        Iterable<AppCustomOperationCode> saved = appService.saveAppCodes(codes);
        int size = 0;
        for (AppCustomOperationCode c: saved) {
            size++;
        }
        return RestUtils.createResult(SUCCEED, size, reqid);
    }
}
