package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.auth.CurrentUser;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.service.ClientInfoService;
import net.arksea.ansible.deploy.api.auth.service.UserDetailsImpl;
import net.arksea.ansible.deploy.api.manage.entity.OperationTrigger;
import net.arksea.ansible.deploy.api.manage.msg.OperationVariable;
import net.arksea.ansible.deploy.api.manage.service.TriggerService;
import net.arksea.restapi.BaseResult;
import net.arksea.restapi.RestResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static net.arksea.ansible.deploy.api.ResultCode.SUCCEED;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@RestController
@RequestMapping(value = "/api/triggers")
public class TriggerController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    private static Logger logger = LogManager.getLogger(TriggerController.class);

    @Autowired
    TriggerService triggerService;
    @Autowired
    ClientInfoService clientInfoService;

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<OperationTrigger> saveTrigger(@RequestBody final OperationTrigger trigger,
                                                    @CurrentUser UserDetailsImpl user,
                                                   final HttpServletRequest httpRequest) {
        ClientInfo info = clientInfoService.getClientInfo(user, httpRequest);
        OperationTrigger saved = triggerService.saveTrigger(info, trigger);
        return new RestResult<>(SUCCEED, saved, httpRequest);
    }

    //-------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('应用:修改')")
    @RequestMapping(path="{triggerId}", method = RequestMethod.DELETE, produces = MEDIA_TYPE)
    public BaseResult deleteTrigger(@PathVariable("triggerId") final long triggerId,
                                 final HttpServletRequest httpRequest) {
        triggerService.delTrigger(triggerId);
        return new BaseResult(SUCCEED, httpRequest);
    }

    //-------------------------------------------------------------------------
    @RequestMapping(path="jobs", method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Long> onTrigger(@RequestParam final Map<String,String> params,
                                      final HttpServletRequest httpRequest) {
        logger.debug("params: {}", () -> params.entrySet().stream()
                .map(e -> e.getKey()+"="+e.getValue())
                .reduce((m, n) -> m +";"+ n).orElse(""));
        Set<OperationVariable> vars = new HashSet<>();
        String token = "";
        String projectTag = "";
        List<String> targetHosts = null;
        for (Map.Entry<String, String> e: params.entrySet()) {
            String key = e.getKey();
            switch (key) {
                case "token":
                    token = e.getValue();
                    break;
                case "projectTag":
                    projectTag = e.getValue();
                    break;
                case "hosts":
                    targetHosts = Arrays.asList(StringUtils.split(e.getValue(), ",;"));
                    break;
                default:
                    OperationVariable v = new OperationVariable();
                    v.setName(e.getKey());
                    v.setValue(e.getValue());
                    vars.add(v);
                    break;
            }
        }
        long jobId = triggerService.onTrigger(projectTag, token, vars, targetHosts);
        return new RestResult<>(SUCCEED, jobId, httpRequest);
    }

}
