package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.PortType;

/**
 * Create by xiaohaixing on 2020/9/21
 */
public class AppPort {
    public final PortType portType;
    public final String name;

    public AppPort(PortType portType, String name) {
        this.portType = portType;
        this.name = name;
    }
}
