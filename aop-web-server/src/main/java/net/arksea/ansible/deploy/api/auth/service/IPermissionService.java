package net.arksea.ansible.deploy.api.auth.service;


import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.entity.Permission;
import net.arksea.ansible.deploy.api.auth.info.PermissionInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface IPermissionService {
    List<PermissionInfo> getPermissionList();
    Pair<String, Permission> savePermission(ClientInfo clientInfo, Long id, PermissionInfo permissionInfo);
}
