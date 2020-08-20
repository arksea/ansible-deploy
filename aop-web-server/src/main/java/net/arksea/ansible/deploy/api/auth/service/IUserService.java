package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.auth.info.ClientInfo;
import net.arksea.ansible.deploy.api.auth.info.GetUserList;
import org.apache.commons.lang3.tuple.Pair;

public interface IUserService {

    GetUserList.Response getUserList(int page, int pageSize);
    Pair<String, User> saveUserRole(ClientInfo clientInfo, long userId, Long[] roleIds);
}
