package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserService implements IUserService {
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());
    @Autowired
    UserDao userDao;

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

    public User getUserByName(String name) {
        try {
            return userDao.findOneByName(name);
        } catch (Exception ex) {
            throw new ServiceException("查询用户失败", ex);
        }
    }
}
