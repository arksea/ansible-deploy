package net.arksea.ansible.deploy.api.manage.service;

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
            def.setDefaultValue("localhost");
            def.setFormLabel("Tomcat 域名");
            def.setInputAddon("");
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("context_path");
            def.setDefaultValue("");
            def.setFormLabel("Tomcat ContextPath (URL路径)");
            def.setInputAddon("http://your_app_domain/");
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("http_port");
            def.setFormLabel("HTTP端口");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.HTTP_ID));
            appVarDefineDao.save(def);
        }
        if (!type.getName().equals("Command")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("server_port");
            boolean isTomcat = type.getName().equals("Tomcat");
            String formLabel = isTomcat?"服务管理端口":"应用服务端口";
            def.setFormLabel(formLabel);
            int portId = isTomcat?PortTypeConfiger.COMMON_ID:PortTypeConfiger.SERVER_ID;
            def.setPortType(portTypeDao.findOne(portId));
            appVarDefineDao.save(def);
        }
        if (!type.getName().equals("Command")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("jmx_port");
            def.setFormLabel("JMX端口");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.JMX_ID));
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("ajp_port");
            def.setFormLabel("AJP协议端口");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.COMMON_ID));
            appVarDefineDao.save(def);
        }
        if (type.getName().equals("Tomcat")) {
            AppVarDefine def = new AppVarDefine();
            def.setAppTypeId(type.getId());
            def.setName("https_port");
            def.setFormLabel("HTTPS端口");
            def.setPortType(portTypeDao.findOne(PortTypeConfiger.COMMON_ID));
            appVarDefineDao.save(def);
        }

    }
}
