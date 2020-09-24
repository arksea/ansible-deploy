package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppTypeDao;
import net.arksea.ansible.deploy.api.manage.dao.PortTypeDao;
import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Component
public class ConfigDataInitializer {

    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    PortTypeDao portTypeDao;

    //初始化静态配置表
    @PostConstruct
    public void init() {
        initPortTypeTable();
        initAppType();
    }

    private void initAppType() {
        long count = appTypeDao.count();
        if (count == 0) {
            AppType t1 = new AppType();
            t1.setName("Tomcat");
            appTypeDao.save(t1);
            AppType t2 = new AppType();
            t2.setName("JavaServer");
            appTypeDao.save(t2);
            AppType t3 = new AppType();
            t3.setName("Command");
            appTypeDao.save(t3);
        }
    }

    public void initPortTypeTable() {
        List<PortType> types = PortTypeConfiger.get();
        long count = portTypeDao.count();
        if (count == 0) {
            for (PortType t: types) {
                portTypeDao.save(t);
            }
        }
    }
}
