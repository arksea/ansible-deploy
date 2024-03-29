package net.arksea.ansible.deploy.api.manage.rest;

import static net.arksea.ansible.deploy.api.ResultCode.*;
import net.arksea.ansible.deploy.api.manage.dao.AppOperationDao;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationCode;
import net.arksea.ansible.deploy.api.manage.service.OperationsService;
import net.arksea.restapi.RestException;
import net.arksea.restapi.RestResult;
import net.arksea.restapi.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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

    @PreAuthorize("hasAuthority('操作管理:查询')")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppOperation>> getOperations(final HttpServletRequest httpRequest) {
        Iterable<AppOperation> list = operationsService.getAll();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(SUCCEED, list, reqid);
    }

    @PreAuthorize("hasAuthority('操作管理:查询')")
    @RequestMapping(path="{id}", method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<AppOperation> getOperationsById(
            @PathVariable("id") long id,
            final HttpServletRequest httpRequest) {
        Optional<AppOperation> optOp = operationDao.findById(id);
        if (optOp.isPresent()) {
            String reqid = (String) httpRequest.getAttribute("restapi-requestid");
            return new RestResult<>(SUCCEED, optOp.get(), reqid);
        } else {
            throw new RestException("查询的操作不存在");
        }
    }

    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<AppOperation> save(@RequestBody final AppOperation operation,
                           final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppOperation saved = operationsService.saveOperation(operation);
        return new RestResult<>(SUCCEED, saved, reqid);
    }

    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(path="{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String delete(@PathVariable("id") final long id, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        operationDao.deleteById(id);
        return RestUtils.createResult(SUCCEED, reqid);
    }

    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(path="codes", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public String saveOperationCode(@RequestBody final AppOperationCode code,
                       final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        AppOperationCode saved = operationsService.saveOperationCode(code);
        return RestUtils.createResult(SUCCEED, saved.getId(), reqid);
    }

    @PreAuthorize("hasAuthority('操作管理:修改')")
    @RequestMapping(path="codes/{id}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public String deleteOperationCode(@PathVariable(name = "id") final Long id, final HttpServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        operationsService.deleteOperationCode(id);
        return RestUtils.createResult(SUCCEED, "", reqid);
    }
}
