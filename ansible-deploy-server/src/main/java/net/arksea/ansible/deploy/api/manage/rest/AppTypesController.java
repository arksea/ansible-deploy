package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.service.AppTypesService;
import net.arksea.ansible.deploy.api.manage.service.OperationsService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestResult;
import static net.arksea.ansible.deploy.api.ResultCode.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by xiaohaixing on 2020/8/28
 */
@RestController
@RequestMapping(value = "/api/appTypes")
public class AppTypesController {

    @Autowired
    AppTypesService appTypesService;

    @Autowired
    OperationsService operationsService;

    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:查询') or hasAuthority('操作管理:查询')")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppType>> getAppTypes(HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, appTypesService.findAll(), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:查询') or hasAuthority('操作管理:查询')")
    @RequestMapping(path="{typeId}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<AppType> findOne(@PathVariable(name = "typeId") final long typeId,
                                       HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, appTypesService.findOne(typeId), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<AppType> saveAppType(@RequestBody AppType body, HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, this.appTypesService.saveAppType(body), httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(path="{typeId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult delAppType(@PathVariable(name="typeId") final long typeId,
                                 HttpServletRequest httpRequest) {
        appTypesService.deleteAppType(typeId);
        return new BaseResult(SUCCEED, httpRequest);
    }
    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:查询') or hasAuthority('操作管理:查询')")
    @RequestMapping(path="{appTypeId}/operations", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppOperation>> getOperationsByAppTypeId(
                @PathVariable(name="appTypeId") Long appTypeId,
                final HttpServletRequest httpRequest) {
        return new RestResult<>(SUCCEED, operationsService.getByAppTypeId(appTypeId), httpRequest);
    }

}
