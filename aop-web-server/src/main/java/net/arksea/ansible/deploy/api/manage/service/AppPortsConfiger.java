package net.arksea.ansible.deploy.api.manage.service;

import java.util.LinkedList;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/21
 */
public class AppPortsConfiger {
    public static List<AppPort> get(String appType) {
        switch (appType) {
            case "tomcat":
                return tomcat();
            default:
                throw new RuntimeException("Unsupported app type: "+appType);
        }
    }
    public static List<AppPort> tomcat() {
        List<AppPort> list = new LinkedList<>();
        list.add(new AppPort(PortTypeConfig.HTTP_ID, "http_port"));
        list.add(new AppPort(PortTypeConfig.COMMON_ID, "server_port"));
        list.add(new AppPort(PortTypeConfig.JMX_ID, "jmx_port"));
        list.add(new AppPort(PortTypeConfig.COMMON_ID, "ajp_port"));
        list.add(new AppPort(PortTypeConfig.COMMON_ID, "https_port"));
        return list;
    }
    public static List<AppPort> javaServer() {
        List<AppPort> list = new LinkedList<>();
        list.add(new AppPort(PortTypeConfig.SERVER_ID, "server_port"));
        list.add(new AppPort(PortTypeConfig.JMX_ID, "jmx_port"));
        return list;
    }
}
