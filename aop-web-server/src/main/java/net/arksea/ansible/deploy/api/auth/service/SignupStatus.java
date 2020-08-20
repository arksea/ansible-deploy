package net.arksea.ansible.deploy.api.auth.service;

/**
 *
 * Created by xiaohaixing on 2017/11/1.
 */
public enum SignupStatus {
    SUCCEED(0), FAILED(1), USERNAME_EXISTS(2);
    private int value;
    SignupStatus(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
