package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.User;


/**
 * Create by xiaohaixing on 2020/4/30
 */
public interface ISignupService {
    User signup(SignupInfo info);
}
