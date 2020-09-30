package net.arksea.ansible.deploy.api.operator.rest;

import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.restapi.RestResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by xiaohaixing on 2020/09/25.
 */
@RestController
@RequestMapping(value = "/api/jobs")
public class JobController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @RequestMapping(method = RequestMethod.POST, produces = MEDIA_TYPE)
    public RestResult<Long> createJob(@RequestBody final OperationJob job, final HttpServletRequest httpRequest) {

        return null;
    }
}
