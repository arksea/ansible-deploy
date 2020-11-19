package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.manage.service.UsersService;
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
    @Autowired
    private UsersService usersService;

    public User signup(SignupInfo info) {
        if (userDao.existsByName(info.getName())) {
            throw new ServiceException("注册失败，用户名已被使用");
        }
        if (!usersService.getOpenRegister()) {
            throw new ServiceException("系统未开放注册，请联系管理员创建账号");
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
            long defaultRole = "admin".equals(info.getName()) ? 1L : 2L;
            Role role = roleDao.findOne(defaultRole);
            if (role != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
            }
            User saved = userDao.save(user);
            logger.info("SignUp succeed， name={}, email={}", info.getName(), info.getEmail());
            return saved;
        } catch (Exception ex) {
            logger.warn("SignUp failed: name={}, email={}", info.getName(), info.getEmail(), ex);
            throw new ServiceException("注册失败", ex);
        }
    }
}
