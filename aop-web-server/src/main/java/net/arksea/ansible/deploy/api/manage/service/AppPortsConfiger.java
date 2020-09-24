package net.arksea.ansible.deploy.api.manage.service;

import java.util.LinkedList;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/21
 */
public class AppPortsConfiger {
    public static List<AppPort> get(String appType) {
        switch (appType) {
            case "Tomcat":
                return tomcat();
            case "JavaServer":
                return javaServer();
            case "Command":
                return command();
            default:
                throw new RuntimeException("不支持的应用类型: "+appType);
        }
    }
    private static List<AppPort> tomcat() {
        List<AppPort> list = new LinkedList<>();
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.HTTP_ID), "http_port"));
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.COMMON_ID), "server_port"));
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.JMX_ID), "jmx_port"));
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.COMMON_ID), "ajp_port"));
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.COMMON_ID), "https_port"));
        return list;
    }
    private static List<AppPort> javaServer() {
        List<AppPort> list = new LinkedList<>();
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.SERVER_ID), "server_port"));
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.JMX_ID), "jmx_port"));
        return list;
    }
    private static List<AppPort> command() {
        List<AppPort> list = new LinkedList<>();
        list.add(new AppPort(PortTypeConfiger.get(PortTypeConfiger.JMX_ID), "jmx_port"));
        return list;
    }
}
