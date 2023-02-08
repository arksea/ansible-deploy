package net.arksea.ansible.deploy.api.operator.service;

/**
 * Create by xiaohaixing on 2020/10/12
 */
public interface IJobEventListener {
    void log(String str);
    default void onFinished() {};
}
