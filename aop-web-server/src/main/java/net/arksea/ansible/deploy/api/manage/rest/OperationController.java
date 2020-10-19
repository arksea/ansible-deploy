package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.ResultCode;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationCode;
import net.arksea.ansible.deploy.api.manage.service.OperationsService;
import net.arksea.restapi.RestException;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by xiaohaixing on 2020/9/25
 */
@RestController
@RequestMapping(value = "/api/operations")
public class OperationController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    OperationsService operationsService;
    @Autowired
    AppOperationDao operationDao;

    @RequiresPermissions("操作管理:查询")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppOperation>> getOperationsByAppTypeId(
            @RequestParam(name="appTypeId",required = false) Long appTypeId,
            final HttpServletRequest httpRequest) {
        Iterable<AppOperation> list = appTypeId == null ? operationsService.getAll() : operationsService.getByAppTypeId(appTypeId);
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, list, reqid);
    }

    @RequiresPermissions("操作管理:查询")
    @RequestMapping(path="{id}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<AppOperation> getOperationsById(
            @PathVariable("id") long id,
            final HttpServletRequest httpRequest) {
        AppOperation op = operationDao.findOne(id);
        if (op == null) {
            throw new RestException("查询的操作不存在");
        } else {
            String reqid = (String) httpRequest.getAttribute("restapi-requestid");
            return new RestResult<>(0, op, reqid);
        }
    }

    @RequiresPermissions("操作管理:修改")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<AppOperation> save(@RequestBody final AppOperation operation,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppOperation saved = operationsService.saveOperation(operation);
        return new RestResult<>(0, saved, reqid);
    }

    @RequiresPermissions("操作管理:修改")
    @RequestMapping(path="{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String delete(@PathVariable("id") final long id, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        operationDao.delete(id);
        return RestUtils.createResult(ResultCode.SUCCEED, reqid);
    }

    @RequiresPermissions("操作管理:修改")
    @RequestMapping(path="codes", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveOperationCode(@RequestBody final AppOperationCode code,
                       final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppOperationCode saved = operationsService.saveOperationCode(code);
        return RestUtils.createResult(ResultCode.SUCCEED, saved.getId(), reqid);
    }

    @RequiresPermissions("操作管理:修改")
    @RequestMapping(path="codes/{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteOperationCode(@PathVariable(name = "id") final Long id, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        operationsService.deleteOperationCode(id);
        return RestUtils.createResult(ResultCode.SUCCEED, "", reqid);
    }
}
