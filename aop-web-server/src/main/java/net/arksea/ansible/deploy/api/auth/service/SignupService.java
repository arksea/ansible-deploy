package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by xiaohaixing on 2020/04/30.
 */
@Component
@Transactional
public class SignupService implements ISignupService {

    private static Logger logger = LogManager.getLogger(SignupService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;


    public Pair<SignupStatus, User> signup(SignupInfo info) {
        if (userDao.existsByName(info.getName())) {
            return Pair.of(SignupStatus.USERNAME_EXISTS, null);
        }
        try {
            String salt = CredentialsMatcherImpl.createSalt();
            String pwdHash = CredentialsMatcherImpl.hashPassword(info.getPassword().toCharArray(), salt);
            User user = new User();
            user.setName(info.getName());
            user.setEmail(info.getEmail());
            user.setPlainPassword(info.getPassword());
            user.setPassword(pwdHash);
            user.setSalt(salt);
            Date today = new Date();
            user.setRegisterDate(today);
            user.setLastLogin(today);
            if ("admin".equals(info.getName())) {
                Role role = roleDao.findOne(1L);
                if (role != null) {
                    Set<Role> roles = new HashSet<>();
                    roles.add(role);
                    user.setRoles(roles);
                }
            }
            User saved = userDao.save(user);
            logger.info("SignUp succeed， name={}, email={}", info.getName(), info.getEmail());
            return Pair.of(SignupStatus.SUCCEED, saved);
        } catch (Exception ex) {
            logger.warn("SignUp failed: name={}, email={}", info.getName(), info.getEmail(), ex);
            return Pair.of(SignupStatus.FAILED, null);
        }
    }
}
