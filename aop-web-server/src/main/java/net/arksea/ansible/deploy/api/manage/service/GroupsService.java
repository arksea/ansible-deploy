package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.dao.HostDao;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;


/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class GroupsService {

    @Autowired
    AppGroupDao appGroupDao;

    @Autowired
    HostDao hostDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AppDao appDao;

    @Transactional
    public AppGroup createGroup(String name, String description) {
        try {
            AppGroup group = new AppGroup();
            group.setName(name);
            group.setDescription(description);
            group.setEnabled(true);
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

    public AppGroup getAppGroupById(long id) {
        try {
            return appGroupDao.findOne(id);
        } catch (Exception ex) {
            throw new RestException("查询组信息失败", ex);
        }
    }

    public Iterable<AppGroup> getUserGroups(long userId) {
        try {
            return appGroupDao.findByUserId(userId);
        } catch (Exception ex) {
            throw new RestException("查询组信息失败", ex);
        }
    }

    @Transactional
    public void deleteAppGroup(long id) {
        try {
            appGroupDao.deleteById(id);
        } catch (Exception ex) {
            throw new RestException("删除组失败", ex);
        }
    }

    @Transactional
    public void addHost(long groupId, long hostId) {
        try {
            Host host = hostDao.findOne(hostId);
            if (host.getAppGroupId() != null) {
                throw new ServiceException("主机已分配给分组："+groupId);
            }
            host.setAppGroupId(groupId);
            hostDao.save(host);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("向分组添加主机失败", ex);
        }
    }

    @Transactional
    public void removeHost(long groupId, long hostId) {
        try {
            Host host = hostDao.findOne(hostId);
            if (host.getAppGroupId() == null) {
                return;
            }
            if (host.getAppGroupId() != groupId) {
                throw new ServiceException("主机不属于指定分组");
            }
            host.setAppGroupId(null);
            hostDao.save(host);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("从分组移除主机失败", ex);
        }
    }

    @Transactional
    public void addMember(long groupId, long userId) {
        try {
            if (appGroupDao.userInGroup(groupId, userId) == 0) {
                appGroupDao.addUserToGroup(groupId, userId);
            }
        } catch (Exception ex) {
            throw new RestException("向分组添加成员失败", ex);
        }
    }

    @Transactional
    public void removeMember(long groupId, long userId) {
        try {
            appGroupDao.removeUserFromGroup(groupId, userId);
        } catch (Exception ex) {
            throw new RestException("从分组移除成员失败", ex);
        }
    }

    @Transactional
    public void addApp(long groupId, long appId) {
        try {
            App app = appDao.findOne(appId);
            if (app.getAppGroupId() != null) {
                throw new ServiceException("应用已属于分组："+app.getAppGroupId());
            }
            app.setAppGroupId(groupId);
            appDao.save(app);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("向分组添加应用失败", ex);
        }
    }

    @Transactional
    public void removeApp(long groupId, long appId) {
        try {
            App app = appDao.findOne(appId);
            if (app.getAppGroupId() == null) {
                return;
            }
            if (app.getAppGroupId() != groupId) {
                throw new ServiceException("应用不属于指定分组");
            }
            app.setAppGroupId(null);
            appDao.save(app);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("从分组移除应用失败", ex);
        }
    }
}
