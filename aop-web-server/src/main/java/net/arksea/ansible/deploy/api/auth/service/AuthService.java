package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.dao.PermissionDao;
import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.Permission;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.restapi.RestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by xiaohaixing on 2020/4/30
 */
@Service("authService")
public class AuthService implements IAuthService {
    private static Logger logger = LogManager.getLogger(AuthService.class);
    private static final int MAX_PERMISSION_LEVEL = 5; //权限有父节点属性，所以限制最大嵌套层级，防止出现死循环
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionDao permissionDao;

    private Map<String,Set<Permission>> rolePerms = new HashMap<>();

    @PostConstruct
    public void init() {
        roleDao.findAll().forEach(this::initRolePerms);
    }

    private void initRolePerms(Role role) {
        Set<Permission> perms = new HashSet<>();
        role.getPermissions().forEach(p->{
            perms.add(p);
            addAllChildPerm(p, perms, 1);
        });
        this.rolePerms.put(role.getRole(), perms);
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            perms.forEach(p -> sb.append(p.getPermission()).append("\n"));
            logger.debug("role {} permissions(Base64Encoded):{}",
                    role.getId(),
                    Base64.encodeBase64String(sb.toString().getBytes()));
        }
    }

    private void addAllChildPerm(Permission p, Set<Permission> perms, int level) {
        if (level <= MAX_PERMISSION_LEVEL) {
            permissionDao.findByPid(p.getId()).forEach(c -> {
                perms.add(c);
                addAllChildPerm(c, perms, level+1);
            });
        } else {
            logger.warn("权限层级超限，请检查配置是否有误，比如出现循环嵌套的情况: id={},perm={}", p.getId(), p.getPermission());
        }
    }

    public User getUserByName(String name) {
        return userDao.findOneByName(name);
    }

    public Set<String> getRolesByUserName(String name) {
        User user = userDao.findOneByName(name);
        return user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet());
    }

    public Set<String> getPermissionsByUserId(long id) {
        User user = userDao.findOne(id);
        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED);
        }
        Set<Role> roleSet = user.getRoles();
        return getPermissionsByRoles(roleSet);
    }

    public Set<String> getPermissionsByUserName(String name) {
        User user = userDao.findOneByName(name);
        Set<Role> roleSet = user.getRoles();
        return getPermissionsByRoles(roleSet);
    }

    public Set<String> getPermissionsByRoles(Set<Role> roles) {
        return getByRoles(roles.stream().map(Role::getRole).collect(Collectors.toList()));
    }

    private Set<String> getByRoles(Collection<String> roles) {
        Set<String> perms = new HashSet<>();
        roles.forEach(r -> perms.addAll(rolePerms.get(r).stream().map(Permission::getPermission).collect(Collectors.toList())));
        return perms;
    }

    public Map<String, Set<String>> getAllPermissionsChilds() {
        Map<String, Set<String>> map = new HashMap<>();
        permissionDao.findAll().forEach(p -> {
            Set<Permission> childs = new HashSet<>();
            addAllChildPerm(p, childs, 1);
            Set<String> c = childs.stream().map(Permission::getPermission).collect(Collectors.toSet());
            if (c.size() > 0) {
                map.put(p.getPermission(), c);
            }
        });
        return map;
    }

    @Override
    public void reloadAuth() {
        this.rolePerms.clear();
        this.init();
    }
}
