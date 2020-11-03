package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppVarDefineDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Component
public class ConfigDataInitializer {

    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    PortTypeDao portTypeDao;
    @Autowired
    AppVarDefineDao appVarDefineDao;

    //初始化静态配置表
    @PostConstruct
    public void init() {
    }
}
