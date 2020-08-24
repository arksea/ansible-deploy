package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class ManageService {

    public static class AppGroupInfo {
        private String name;       // 分组名称
        private String description;// 分组描述
        private String avatar;     // 分组头像
        private int appCount;      // 分组管理的应用
        private int hostCount;     // 分组管理的主机
        private int userCount;     // 加入分组的用户

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getAppCount() {
            return appCount;
        }

        public void setAppCount(int appCount) {
            this.appCount = appCount;
        }

        public int getHostCount() {
            return hostCount;
        }

        public void setHostCount(int hostCount) {
            this.hostCount = hostCount;
        }

        public int getUserCount() {
            return userCount;
        }

        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }
    }

    @Autowired
    AppGroupDao appGroupDao;

    @Transactional
    public AppGroup createGroup(String name, String description) {
        try {
            AppGroup group = new AppGroup();
            group.setName(name);
            group.setDescription(description);
            return appGroupDao.save(group);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException("新建组失败, 可能组名重复或过长", ex);
        } catch (Exception ex) {
            throw new RestException("新建组失败", ex);
        }
    }

    public Iterable<AppGroup> getAppGroups() {
        try {
            return appGroupDao.findAll();
        } catch (Exception ex) {
            throw new RestException("查询组信息失败", ex);
        }
    }
}
