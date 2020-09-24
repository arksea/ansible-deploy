package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppTypeDao;
import net.arksea.ansible.deploy.api.manage.dao.AppVarDefineDao;
import net.arksea.ansible.deploy.api.manage.dao.PortTypeDao;
import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;
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
    @Autowired
    AppVarDefineDao appVarDefineDao;

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
            AppType t1saved = appTypeDao.save(t1);
            initAppVarDefine(t1saved);
            AppType t2 = new AppType();
            t2.setName("JavaServer");
            AppType t2saved = appTypeDao.save(t2);
            initAppVarDefine(t2saved);
            AppType t3 = new AppType();
            t3.setName("Command");
            AppType t3saved = appTypeDao.save(t3);
            initAppVarDefine(t3saved);
        }
    }

    private void initPortTypeTable() {
        long count = portTypeDao.count();
        if (count == 0) {
            List<PortType> types = PortTypeConfiger.get();
            for (PortType t: types) {
                portTypeDao.save(t);
            }
        }
    }

    private void initAppVarDefine(AppType type) {
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("domain");
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("context_path");
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("http_port");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.HTTP_ID));
            appVarDefineDao.save(def);
        }
        if (!type.getName().equals("Command")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("server_port");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.SERVER_ID));
            appVarDefineDao.save(def);
        }
        if (!type.getName().equals("Command")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("jmx_port");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.JMX_ID));
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("ajp_port");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.COMMON_ID));
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("https_port");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.COMMON_ID));
        }

    }
}
