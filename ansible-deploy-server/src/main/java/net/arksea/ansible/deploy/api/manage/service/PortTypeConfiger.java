package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.PortType;

import java.util.LinkedList;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/21
 */

public class PortTypeConfiger {

    public static final long HTTP_ID   = 1;
    public static final long COMMON_ID = 2;
    public static final long SERVER_ID = 3;
    public static final long JMX_ID = 4;

    public static List<PortType> get() {
        List<PortType> list = new LinkedList<>();
        list.add(get(HTTP_ID));
        list.add(get(COMMON_ID));
        list.add(get(SERVER_ID));
        list.add(get(JMX_ID));
        return list;
    }

    public static PortType get(long id) {
        if (id == HTTP_ID) {
            PortType p1 = new PortType();
            p1.setId(HTTP_ID);
            p1.setName("HTTP");
            p1.setDescription("Web网站或Http接口");
            return p1;
        } else if (id == COMMON_ID) {
            PortType p2 = new PortType();
            p2.setId(COMMON_ID);
            p2.setName("通用");
            p2.setDescription("通用端口");
            return p2;
        } else if (id == SERVER_ID) {
            PortType p3 = new PortType();
            p3.setId(SERVER_ID);
            p3.setName("应用");
            p3.setDescription("应用服务端口");
            return p3;
        } else if (id == JMX_ID) {
            PortType p4 = new PortType();
            p4.setId(JMX_ID);
            p4.setName("JMX");
            p4.setDescription("JMX监控");
            return p4;
        } else {
            throw new RuntimeException("Unsupport port type:" + id);
        }
    }
}
