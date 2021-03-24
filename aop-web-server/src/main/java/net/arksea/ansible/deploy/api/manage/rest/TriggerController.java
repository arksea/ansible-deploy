package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import net.arksea.ansible.deploy.api.manage.service.TriggerService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api/triggers")
public class TriggerController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    TriggerService triggerService;
    @Autowired
    ClientInfoService clientInfoService;

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<OperationTrigger> addTrigger(@RequestBody final OperationTrigger trigger,
                                                   final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(httpRequest);
        OperationTrigger saved = triggerService.addTrigger(info, trigger);
        return new RestResult<>(SUCCEED, saved, httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequiresPermissions("应用:修改")
    @RequestMapping(path="{triggerId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult deleteTrigger(@PathVariable("triggerId") final long triggerId,
                                 final HttpServletRequest httpRequest) {
        triggerService.delTrigger(triggerId);
        return new BaseResult(SUCCEED, httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequestMapping(path="job", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Long> onTrigger(@RequestBody final String token,
                                      final HttpServletRequest httpRequest) {
        long jobId = triggerService.onTrigger(token);
        return new RestResult<>(SUCCEED, jobId, httpRequest);
    }

}
