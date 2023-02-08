package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ClientInfoService implements IUserService {
    @Autowired
    UserDao userDao;

    public ClientInfo getClientInfo(UserDetailsImpl currentUser, HttpServletRequest httpRequest) {
        final User user = userDao.findOneByName(currentUser.getUsername());
        if (user == null) {
            throw new ServiceException("获取用户信息失败，user=" + currentUser.getUsername());
        }
        final String remoteIp = httpRequest.getRemoteAddr();
        return new ClientInfo(user.getId(), user.getName(), remoteIp);
    }
}
