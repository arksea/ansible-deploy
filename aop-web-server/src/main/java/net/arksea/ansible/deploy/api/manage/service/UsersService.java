package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class UsersService {

    @Autowired
    UserDao userDao;

    public Iterable<User> getUsers(boolean active) {
        try {
            return userDao.findAllByLocked(!active);
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
}
