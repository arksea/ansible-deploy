package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.msg.AppInfo;
import net.arksea.ansible.deploy.api.manage.msg.GetOperationJobHistory;
import net.arksea.ansible.deploy.api.manage.service.AppService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.ErrorResult;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static net.arksea.ansible.deploy.api.ResultCode.FAILED;
import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

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
        App app = appService.createAppTemplate(typeName);
        return new RestResult<>(SUCCEED, app, httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE, consumes = MEDIA_TYPE)
    public RestResult<App> save(@RequestBody final App app,final HttpServletRequest httpRequest) {
        App stored = appService.save(app);
        return new RestResult<>(SUCCEED, stored, httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{appId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getAppById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        App app = appService.findOne(appId);
        if (app == null) {
            return new ErrorResult(FAILED, httpRequest, "应用不存在");
        } else {
            return new RestResult<>(SUCCEED, app, httpRequest);
        }
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{appId}/codes", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getAppCodesById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, appService.findAppCodes(appId), httpRequest);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{appId}/info", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public BaseResult getApptagById(@PathVariable("appId") long appId, HttpServletRequest httpRequest) {
        App app = appService.findOne(appId);
        if (app == null) {
            return new ErrorResult(FAILED, httpRequest, "应用不存在");
        } else {
            AppInfo info = new AppInfo(app.getId(), app.getApptag(), app.getAppType().getId());
            return new RestResult<>(SUCCEED, info, httpRequest);
        }
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{appId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public RestResult<Long> updateDeletedById(@PathVariable("appId") long appId,
                                       HttpServletRequest httpRequest) {
        appService.deletedById(appId);
        return new RestResult<>(SUCCEED, appId, httpRequest);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="notInGroup", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<App>> getAppsNotInGroup(HttpServletRequest httpRequest) {
        Iterable<App> apps = appService.findNotInGroup();
        return new RestResult<>(SUCCEED, apps, httpRequest);
    }
    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:查询")
    @RequestMapping(path="{appId}/operations", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<GetOperationJobHistory.Response> getAppOperationHistory(
                    @PathVariable("appId") long appId,
                    @RequestParam int page, @RequestParam int pageSize,
                    @RequestParam(required = false) String startTime,
                    @RequestParam(required = false) String endTime,
                    @RequestParam(required = false) String operator,
                    HttpServletRequest httpRequest) {
        GetOperationJobHistory.Request query = new GetOperationJobHistory.Request(appId,page,pageSize,startTime,endTime,operator);
        GetOperationJobHistory.Response infos = appService.findOperationJobInfos(query);
        return new RestResult<>(SUCCEED, infos, httpRequest);
    }
}
