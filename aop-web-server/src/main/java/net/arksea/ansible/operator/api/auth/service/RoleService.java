package net.arksea.ansible.operator.api.auth.service;

import net.arksea.ansible.operator.api.auth.dao.RoleDao;
import net.arksea.ansible.operator.api.auth.dao.PermissionDao;
import net.arksea.ansible.operator.api.auth.entity.OperationTypeEnum;
import net.arksea.ansible.operator.api.auth.entity.Permission;
import net.arksea.ansible.operator.api.auth.entity.ResourceTypeEnum;
import net.arksea.ansible.operator.api.auth.entity.Role;
import net.arksea.ansible.operator.api.auth.info.ClientInfo;
import net.arksea.ansible.operator.api.auth.info.GetRoleList;
import net.arksea.ansible.operator.api.auth.info.RoleInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RoleService implements IRoleService {

    @Autowired
    RoleDao roleDao;
    @Autowired
    PermissionDao permissionDao;

    @Autowired
    IOperationLogService iOperationLogService;

    @Override
    public GetRoleList.Response getRoleList(int page, int pageSize) {
        int pageVal = page < 1 ? 0 : page - 1;
        Pageable pageable = new PageRequest(pageVal, pageSize);
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
        Page<Role> rolePage = roleDao.findAll(specification, pageable);
        List<RoleInfo> roleList = new ArrayList<>();
        for (Role role : rolePage.getContent()) {
            int available = role.isAvailable() ? 1 : 0;
            roleList.add(new RoleInfo(role.getId(), available, role.getDescription(), role.getRole(), role.getPermissions()));
        }
        return new GetRoleList.Response(rolePage.getTotalElements(), rolePage.getTotalPages(), roleList);
    }

    @Override
    public Pair<String, Role> saveRole(ClientInfo clientInfo, Long roleId, String roleName, String description) {

        List<Role> existList = roleDao.findByRole(roleName);
        Role role = null;
        OperationTypeEnum opType = OperationTypeEnum.ADD;
        String originalContent = "无";
        if (roleId == null) {
            if (existList.size() > 0) {
                return Pair.of("添加失败，存在相同名称的角色", null);
            }
            role = new Role();
            role.setAvailable(true);
            role.setDescription(description);
            role.setRole(roleName);

        } else {
            boolean hasRepeat = false;
            for (Role existRole : existList) {
                if (existRole.getId() != roleId) {
                    hasRepeat = true;
                    break;
                }
            }
            if (hasRepeat) {
                return Pair.of("修改失败，存在其他相同名称的角色", null);
            }
            role = roleDao.findOne(roleId);
            if (role == null) {
                return Pair.of("修改失败，未查询到所要编辑的角色记录", null);
            }
            opType = OperationTypeEnum.UPDATE;
            originalContent = role.makeContent();
            role.setDescription(description);
            role.setRole(roleName);
        }
        Role newRole = roleDao.save(role);
        String modifiedContent = newRole.makeContent();
        iOperationLogService.addOperationLog(clientInfo, opType, ResourceTypeEnum.ROLE, newRole.getId(), originalContent, modifiedContent, true);
        return Pair.of("", newRole);
    }

    /**
     * 为角色分配权限节点
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    @Override
    public Pair<String, Role> saveRolePermission(ClientInfo clientInfo, Long roleId, Long[] permissionIds) {
        Role role = roleDao.findOne(roleId);
        if (role == null) {
            return Pair.of("设置失败，未查询到所要编辑的角色记录", null);
        }
        String originalContent = role.makeContent();
        Set<Permission> permissionSet = new HashSet<>();
        for (Long id : permissionIds) {
            Permission permission = permissionDao.findOne(id);
            if (permission == null) {
                return Pair.of("设置失败，未查询到所要设置的权限id=" + id, null);
            }
            permissionSet.add(permission);
        }
        role.setPermissions(permissionSet);

        Role newRole = roleDao.save(role);
        String modifiedContent = newRole.makeContent();
        iOperationLogService.addOperationLog(clientInfo, OperationTypeEnum.UPDATE, ResourceTypeEnum.ROLE, newRole.getId(), originalContent, modifiedContent, true);
        return Pair.of("", role);
    }
}
