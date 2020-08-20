package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.User;
import org.apache.commons.lang3.tuple.Pair;


/**
 * Create by xiaohaixing on 2020/4/30
 */
public interface ISignupService {
    Pair<SignupStatus, User> signup(SignupInfo info);
}
