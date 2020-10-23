package net.arksea.ansible.deploy.api.manage.msg;

import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;

import java.util.Set;

/**
 * Create by xiaohaixing on 2020/10/23
 */
public class AppTypeDefine {

    private AppType appType;
    private Set<AppVarDefine> appVarDefines;

    private AppTypeDefine() {
    }

    public AppTypeDefine(AppType appType, Set<AppVarDefine> appVarDefines) {
        this.appType = appType;
        this.appVarDefines = appVarDefines;
    }

    public AppType getAppType() {
        return appType;
    }

    public Set<AppVarDefine> getAppVarDefines() {
        return appVarDefines;
    }
}
