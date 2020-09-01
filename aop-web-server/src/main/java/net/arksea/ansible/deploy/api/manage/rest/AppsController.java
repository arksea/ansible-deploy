package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.UserService;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/8/28
 */
@RestController
@RequestMapping(value = "/api")
public class AppsController {

    @Autowired
    AppService appService;

    @Autowired
    UserService userService;

    @Autowired

    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @RequiresPermissions("应用:修改")
    @RequestMapping(path="apps", method = RequestMethod.POST, produces = MEDIA_TYPE, consumes = MEDIA_TYPE)
    public String save(@RequestBody final App app,final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        App stored = appService.save(app);
        return RestUtils.createResult(ResultCode.SUCCEED, stored.getId(), reqid);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="apps/{appId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<App> getAppById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        App app = appService.findOne(appId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, app, reqid);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="apps/{appId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public RestResult<Long> delAppById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        appService.updateDeletedById(appId, true);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, appId, reqid);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="user/apps", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<List<App>> getUserApps(HttpServletRequest httpRequest) {
        ClientInfo info = userService.getClientInfo(httpRequest);
        List<App> apps = appService.findByUserId(info.userId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, apps, reqid);
    }

}
