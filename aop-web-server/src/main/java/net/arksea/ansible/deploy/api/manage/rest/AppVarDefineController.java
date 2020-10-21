package net.arksea.ansible.deploy.api.manage.rest;

import net.arksea.ansible.deploy.api.manage.dao.AppVarDefineDao;
import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;
import net.arksea.restapi.RestResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by xiaohaixing on 2020/09/25.
 */
@RestController
@RequestMapping(value = "/api/varDefines")
public class AppVarDefineController {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    AppVarDefineDao appVarDefineDao;

    @RequiresPermissions("应用:查询")
    @RequestMapping(method = RequestMethod.GET, produces = MEDIA_TYPE)
    public RestResult<Iterable<AppVarDefine>> getAppVarDefines(final HttpServletRequest httpRequest) {
        Iterable<AppVarDefine> defines = appVarDefineDao.findAll();
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        return new RestResult<>(0, defines, reqid);
    }
}
