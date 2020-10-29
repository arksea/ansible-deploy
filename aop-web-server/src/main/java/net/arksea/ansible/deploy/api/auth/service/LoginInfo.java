package net.arksea.ansible.deploy.api.auth.service;

/**
 *
 * Created by xiaohaixing on 2017/11/1.
 */
public class LoginInfo {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
