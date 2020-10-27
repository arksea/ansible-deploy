package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.ErrorResult;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by xiaohaixing on 2020/8/28
 */
@RestController
@RequestMapping(value = "/api/apps")
public class AppsController {

    @Autowired
    AppService appService;

    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="template/{typeName}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<App> getAppTemplate(@PathVariable("typeName") String typeName, HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        App app = appService.createAppTemplate(typeName);
        return new RestResult<>(SUCCEED, app, reqid);
    }

    @RequiresPermissions("应用:修改")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE, consumes = MEDIA_TYPE)
    public String save(@RequestBody final App app,final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        App stored = appService.save(app);
        return RestUtils.createResult(SUCCEED, stored, reqid);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{appId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getAppById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        App app = appService.findOne(appId);
        if (app == null) {
            return new ErrorResult(FAILED, reqid, "应用不存在");
        } else {
            return new RestResult<>(SUCCEED, app, reqid);
        }
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{appId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public RestResult<Long> updateDeletedById(@PathVariable("appId") long appId,
                                       HttpServletRequest httpRequest) {
        appService.deletedById(appId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, appId, reqid);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="notInGroup", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<App>> getAppsNotInGroup(HttpServletRequest httpRequest) {
        Iterable<App> apps = appService.findNotInGroup();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, apps, reqid);
    }
    //-------------------------------------------------------------------------

}
