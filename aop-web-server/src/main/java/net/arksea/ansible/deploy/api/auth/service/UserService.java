package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.dao.RoleDao;
import net.arksea.ansible.deploy.api.auth.entity.ResourceTypeEnum;
import net.arksea.ansible.deploy.api.auth.entity.Role;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.OperationTypeEnum;
import net.arksea.ansible.deploy.api.auth.info.GetUserList;
import net.arksea.ansible.deploy.api.auth.info.UserInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
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
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserService implements IUserService {
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());
    @Autowired
    UserDao userDao;
    @Autowired
    RoleDao roleDao;

    @Autowired
    IOperationLogService iOperationLogService;

    @Override
    public GetUserList.Response getUserList(int page, int pageSize) {
        int pageVal = page < 1 ? 0 : page - 1;
        Pageable pageable = new PageRequest(pageVal, pageSize);
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
        Page<User> userPage = userDao.findAll(specification, pageable);
        List<UserInfo> userList = new ArrayList<>();
        for (User user : userPage.getContent()) {
            String lastLoginDate = Timestamp.from(user.getLastLogin().toInstant()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
            String regDate = Timestamp.from(user.getRegisterDate().toInstant()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
            int lock = user.isLocked() ? 1 : 0;
            userList.add(new UserInfo(user.getId(), user.getName(), user.getEmail(), lock, lastLoginDate, regDate, user.getRoles()));
        }
        return new GetUserList.Response(userPage.getTotalElements(), userPage.getTotalPages(), userList);
    }

    /**
     * 修改分配给用户的角色列表
     *
     * @param userId
     * @param roleIds
     * @return
     */
    @Override
    public Pair<String, User> saveUserRole(ClientInfo clientInfo, long userId, Long[] roleIds) {
        User user = userDao.findOne(userId);
        if (user == null) {
            return Pair.of("设置失败，未查询到所要编辑的用户记录", null);
        }
        String originalContent = user.makeContent();
        Set<Role> roleSet = new HashSet<>();
        for (Long id : roleIds) {
            Role role = roleDao.findOne(id);
            if (role == null) {
                return Pair.of("设置失败，未查询到所要设置的角色id=" + id, null);
            }
            roleSet.add(role);
        }
        user.setRoles(roleSet);

        User newUser = userDao.save(user);
        String modifiedContent = newUser.makeContent();
        iOperationLogService.addOperationLog(clientInfo, OperationTypeEnum.UPDATE, ResourceTypeEnum.USER, userId, originalContent, modifiedContent, true);
        return Pair.of("", newUser);
    }

    public ClientInfo getClientInfo(HttpServletRequest httpRequest) {
        Subject subject = SecurityUtils.getSubject();
        Long userId = (Long)subject.getSession().getAttribute("user_id");
        String userName = (String)subject.getSession().getAttribute("user_name");
        if (userId == null || userName == null) {
            //session失效则根据remberMe记录的用户Id重新设置session
            userId = (long)subject.getPrincipal();
            logger.debug("session失效，重新加载，userID={}", userId);
            User user = userDao.findOne(userId);
            if (user == null) {
                throw new UnauthenticatedException("获取用户信息失败，userID=" + userId);
            } else {
                userName = user.getName();
                subject.getSession().setAttribute("user_id", userId);
                subject.getSession().setAttribute("user_name", userName);
            }
        }
        final String remoteIp = httpRequest.getRemoteAddr();
        return new ClientInfo(userId, userName, remoteIp);
    }
}
