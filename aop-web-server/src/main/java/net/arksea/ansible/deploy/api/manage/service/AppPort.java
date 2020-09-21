package net.arksea.ansible.deploy.api.manage.service;

/**
 * Create by xiaohaixing on 2020/9/21
 */
public class AppPort {
    public final int portType;
    public final String name;

    public AppPort(int portType, String name) {
        this.portType = portType;
        this.name = name;
    }
}
