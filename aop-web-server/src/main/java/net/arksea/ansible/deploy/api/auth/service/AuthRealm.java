package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

/**
 * Create by xiaohaixing on 2020/4/29
 */

public class AuthRealm extends AuthorizingRealm {
    private static final Logger logger = LogManager.getLogger(AuthRealm.class.getName());

    private IAuthService authService;

    public IAuthService getAuthService() {
        return authService;
    }

    public void setAuthService(IAuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Long userId = (Long)principals.fromRealm(getName()).iterator().next();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> perms = authService.getPermissionsByUserId(userId);
        info.addStringPermissions(perms);
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            perms.forEach(p -> sb.append(p).append("\n"));
            logger.debug("userId={}, permissions(Base64Encoded)={}", userId,
                    Base64.encodeBase64String(sb.toString().getBytes()));
        }
        return info;
    }
    /**
     * 用户认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) authcToken;
        User user = authService.getUserByName(upToken.getUsername());
        if (user == null) {
            throw new AuthenticationException("用户名或密码错误");
        } else if (user.isLocked()) {
            throw new AuthenticationException("账号已禁用");
        } else {
            return new SimpleAuthenticationInfo(user.getId(), user, getName());
        }
    }

}
