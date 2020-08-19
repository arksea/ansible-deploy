package net.arksea.ansible.operator.api.auth.service;


import net.arksea.ansible.operator.api.auth.entity.Role;
import net.arksea.ansible.operator.api.auth.entity.User;

import java.util.Map;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/5/6
 */
public interface IAuthService {
    User getUserByName(String name);
    Set<String> getRolesByUserName(String name);
    Set<String> getPermissionsByRoles(Set<Role> roles);
    Set<String> getPermissionsByUserId(long id);
    Set<String> getPermissionsByUserName(String name);
    Map<String, Set<String>> getAllPermissionsChilds();
    void reloadAuth();
}