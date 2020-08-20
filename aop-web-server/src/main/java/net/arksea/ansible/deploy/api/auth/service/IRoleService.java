package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.info.GetRoleList;
import org.apache.commons.lang3.tuple.Pair;

public interface IRoleService {
    GetRoleList.Response getRoleList(int page, int pageSize);
    Pair<String, Role> saveRole(ClientInfo clientInfo, Long roleId, String roleName, String description);
    Pair<String, Role> saveRolePermission(ClientInfo clientInfo, Long roleId, Long[] permissionIds);
}
