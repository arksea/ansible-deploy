package net.arksea.ansible.operator.api.auth.service;

import net.arksea.ansible.operator.api.auth.dao.PermissionDao;
import net.arksea.ansible.operator.api.auth.entity.OperationTypeEnum;
import net.arksea.ansible.operator.api.auth.entity.Permission;
import net.arksea.ansible.operator.api.auth.entity.ResourceTypeEnum;
import net.arksea.ansible.operator.api.auth.info.ClientInfo;
import net.arksea.ansible.operator.api.auth.info.PermissionInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PermissionService implements IPermissionService {
    @Autowired
    PermissionDao permissionDao;

    @Autowired
    IOperationLogService iOperationLogService;

    @Override
    public List<PermissionInfo> getPermissionList() {
        List<Permission> list = permissionDao.findByPid(null);

        List<PermissionInfo> resultList = new ArrayList<>();
        list.forEach(p -> {
            int available = p.isAvailable() ? 1 : 0;
            long pid = p.getPid() == null ? 0 : p.getPid();
            int level = 1;
            PermissionInfo info = new PermissionInfo(p.getId(), p.getPermission(), p.getDescription(), available, pid, level);
            List<PermissionInfo> childList = getChildPermissionList(p.getId(), level + 1);
            info.childList.addAll(childList);
            resultList.add(info);
        });
        return resultList;
    }

    private List<PermissionInfo> getChildPermissionList(long pid, int level) {
        List<PermissionInfo> infoList = new ArrayList<>();
        if (level <= 5) {
            List<Permission> list = permissionDao.findByPid(pid);
            list.forEach(p -> {
                int available = p.isAvailable() ? 1 : 0;
                PermissionInfo info = new PermissionInfo(p.getId(), p.getPermission(), p.getDescription(), available, pid, level);
                List<PermissionInfo> childList = getChildPermissionList(p.getId(), level + 1);
                info.childList.addAll(childList);
                infoList.add(info);
            });
        } else {

        }
        return infoList;
    }

    @Override
    public Pair<String, Permission> savePermission(ClientInfo clientInfo, Long id, PermissionInfo permissionInfo) {

        List<Permission> existList = permissionDao.findByPermission(permissionInfo.permission);
        OperationTypeEnum opType = OperationTypeEnum.ADD;
        String originalContent = "无";
        Permission permission = null;
        if (id == null) {
            if (existList.size() > 0) {
                return Pair.of("添加失败，已存在相同的权限项目", null);
            }
            permission = new Permission();
            permission.setAvailable(true);
            permission.setDescription(permissionInfo.description);
            permission.setPermission(permissionInfo.permission);
            permission.setPid(permissionInfo.pid == 0 ? null : permissionInfo.pid);

        } else {
            boolean hasRepeat = false;
            for (Permission other : existList) {
                System.out.println(String.format("Permission %s, %s", other.getId(), id));
                if (!other.getId().equals(id)) {
                    hasRepeat = true;
                    break;
                }
            }
            if (hasRepeat) {
                return Pair.of("编辑失败，已存在相同的权限项目", null);
            }

            permission = permissionDao.findOne(id);
            if (permission == null) {
                return Pair.of("编辑失败，未查询到所要编辑的记录", null);
            }
            opType = OperationTypeEnum.UPDATE;
            originalContent = permission.makeContent();
            permission.setPermission(permissionInfo.permission);
            permission.setDescription(permissionInfo.description);
        }

        Permission newPermission = permissionDao.save(permission);
        iOperationLogService.addOperationLog(clientInfo, opType, ResourceTypeEnum.PERMISSION, newPermission.getId(), originalContent, newPermission.makeContent(), true);
        return Pair.of("", newPermission);
    }
}
