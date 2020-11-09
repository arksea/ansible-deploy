package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.service.CredentialsMatcherImpl;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class UsersService {

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    public Iterable<Role> getRoles() {
        try {
            return roleDao.findAll();
        } catch (Exception ex) {
            throw new RestException("查询角色信息失败", ex);
        }
    }

    public Iterable<User> getUsers(boolean active) {
        try {
            return userDao.findAllByLocked(!active);
        } catch (Exception ex) {
            throw new RestException("查询用户信息失败", ex);
        }
    }

    public Iterable<User> getUsersNotInGroup(long groupId) {
        try {
            return userDao.findUsersNotInGroup(groupId);
        } catch (Exception ex) {
            throw new RestException("查询用户信息失败", ex);
        }
    }

    @Transactional
    public void blockUser(long id) {
        try {
            userDao.lockById(id);
        } catch (Exception ex) {
            throw new RestException("禁用账号失败", ex);
        }
    }

    @Transactional
    public void unblockUser(long id) {
        try {
            userDao.unlockById(id);
        } catch (Exception ex) {
            throw new RestException("启用账号失败", ex);
        }
    }

    @Transactional
    public void updateUserRoles(long userId, List<Long> roleIdList) {
        try {
            User user = userDao.findOne(userId);
            if (user == null) {
                return;
            }
            Set<Role> roleSet = new HashSet<>();
            for (Long id : roleIdList) {
                Role role = roleDao.findOne(id);
                if (role != null) {
                    roleSet.add(role);
                }
            }
            user.setRoles(roleSet);
            User newUser = userDao.save(user);
        } catch (RestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("更新用户角色失败", ex);
        }
    }

    @Transactional
    public String resetUserPassword(Long userId) {
        try {
            User user = userDao.findOne(userId);
            if (user == null) {
                throw new ServiceException("用户不存在: "+userId);
            }
            String pwd =  CredentialsMatcherImpl.createRandom(2);
            String pwdHash = CredentialsMatcherImpl.hashPassword(pwd.toCharArray(), user.getSalt());
            user.setPassword(pwdHash);
            userDao.save(user);
            return pwd;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("重置用户密码失败: "+userId, ex);
        }
    }

    @Transactional
    public void setUserPassword(Long userId, String pwd) {
        try {
            User user = userDao.findOne(userId);
            if (user == null) {
                throw new ServiceException("用户不存在: "+userId);
            }
            String pwdHash = CredentialsMatcherImpl.hashPassword(pwd.toCharArray(), user.getSalt());
            user.setPassword(pwdHash);
            userDao.save(user);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("设置用户密码失败: "+userId, ex);
        }
    }

    public User getUserByName(String name) {
        try {
            return userDao.findOneByName(name);
        } catch (Exception ex) {
            throw new ServiceException("查询用户失败", ex);
        }
    }
}
