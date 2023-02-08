package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);
    private final AuthService authService;

    private final String systemProfile;
    public UserDetailsServiceImpl(AuthService authService, String systemProfile) {
        this.authService = authService;
        this.systemProfile = systemProfile;
    }

    /**
     * 用户登录时，从数据库中获取用户信息
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("test".equals(systemProfile)) {
            return loadTestUserByUsername(username);
        }
        net.arksea.ansible.deploy.api.auth.entity.User user = authService.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("username " + username + " is not found");
        }
        logger.debug("loadUserByUsername: {}, pwd: {}", username, user.getPassword());
        Collection<? extends GrantedAuthority> authorities = authService
                .getPermissionsByUserName(username)
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        //在UserDetails中存储用户角色信息,方便后续从JWT Token中直接恢复鉴权信息,而不需要再次访问数据库
        //在Token中保存用户角色信息,而非权限信息,是为了避免权限信息过多导致Token过大
        List<String> roles = user.getRoles().stream().map(Role::getRole).collect(Collectors.toList());
        return new UserDetailsImpl(user.getName(), user.getPassword(), user.isLocked(), authorities, roles);
    }

    private UserDetails loadTestUserByUsername(String username) {
        return new UserDetailsImpl("user","{noop}password",false,
                Stream.of("应用:查询","系统:查询").map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                Collections.singletonList("系统信息查询"));
    }
}
