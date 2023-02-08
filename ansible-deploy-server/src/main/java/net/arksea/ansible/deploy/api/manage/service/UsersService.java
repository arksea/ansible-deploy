package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.dao.SystemPropertyDao;
import net.arksea.ansible.deploy.api.manage.entity.SystemProperty;
import net.arksea.ansible.deploy.api.manage.msg.GetUsers;
import net.arksea.ansible.deploy.api.system.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class UsersService {
    public static final String OPEN_REGISTER_VAR_NAME = "openRegister";
    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    AppGroupDao appGroupDao;

    @Autowired
    SystemPropertyDao systemPropertyDao;

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public Iterable<Role> getRoles() {
        return roleDao.findAll();
    }

    public GetUsers.Response findUsers(GetUsers.Request msg) {
        int page = msg.page < 1 ? 0 : msg.page - 1;
        Pageable pageable = PageRequest.of(page, msg.pageSize, Sort.Direction.ASC, "name");
        Specification<User> specification = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(msg.nameSearch)) {
                predicateList.add(cb.like(root.get("name"), "%" + msg.nameSearch + "%"));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
        Page<User> users = userDao.findAll(specification, pageable);
        return new GetUsers.Response(users.getTotalElements(),users.getTotalPages(), users.getContent());
    }

    public Iterable<User> getUsersNotInGroup(long groupId) {
        return userDao.findUsersNotInGroup(groupId);
    }

    @Transactional
    public void blockUser(long id) {
        Optional<User> op = userDao.findById(id);
        if (op.isPresent()) {
            if (op.get().getName().equals("admin")) {
                throw new ServiceException("不能禁用Admin账号");
            }
        }
        userDao.lockById(id);
    }

    @Transactional
    public void unblockUser(long id) {
        userDao.unlockById(id);
    }

    @Transactional
    public void deleteUser(long id) {
        appGroupDao.removeUserFromAllGroup(id);
        userDao.deleteById(id);
    }

    @Transactional
    public void updateUserRoles(long userId, List<Long> roleIdList) {
        Optional<User> op = userDao.findById(userId);
        if (!op.isPresent()) {
            return;
        }
        Set<Role> roleSet = new HashSet<>();
        for (Long id : roleIdList) {
            Optional<Role> roleOp = roleDao.findById(id);
            roleOp.ifPresent(roleSet::add);
        }
        User user = op.get();
        user.setRoles(roleSet);
        userDao.save(user);
    }

    @Transactional
    public String resetUserPassword(Long userId) {
        try {
            Optional<User> op = userDao.findById(userId);
            if (!op.isPresent()) {
                throw new ServiceException("用户不存在: "+userId);
            }
            User user = op.get();
            String pwd =  RandomUtils.createRandom(2);
            String pwdHash = passwordEncoder.encode(pwd);
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
    public void modifyUserPassword(Long userId, String oldPwd, String newPwd) {
        try {
            Optional<User> op = userDao.findById(userId);
            if (!op.isPresent()) {
                throw new ServiceException("用户不存在: "+userId);
            }
            User user = op.get();
            if (!passwordEncoder.matches(oldPwd, op.get().getPassword())) {
                throw new ServiceException("输入的旧密码错误");
            }
            String pwdHash = passwordEncoder.encode(newPwd);
            user.setPassword(pwdHash);
            userDao.save(user);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("修改用户密码失败: "+ex.getMessage(), ex);
        }
    }

    public User getUserByName(String name) {
        return userDao.findOneByName(name);
    }

    public boolean getOpenRegister() {
        SystemProperty p = systemPropertyDao.findByName(OPEN_REGISTER_VAR_NAME);
        if (p == null) {
            return true;
        } else {
            return Boolean.parseBoolean(p.getValue());
        }
    }

    @Transactional
    public void setOpenRegistry(boolean status) {
        SystemProperty p = systemPropertyDao.findByName(OPEN_REGISTER_VAR_NAME);
        String value = Boolean.toString(status);
        if (p == null) {
            p = new SystemProperty();
            p.setName(OPEN_REGISTER_VAR_NAME);
            p.setValue(value);
            systemPropertyDao.save(p);
        } else if (!p.getValue().equals(value)) {
            p.setValue(value);
            systemPropertyDao.save(p);
        }
    }
}
