package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.dao.UserDao;
import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.restapi.RestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class GroupsService {
    private static Logger logger = LogManager.getLogger(GroupsService.class);
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
            return appGroupDao.save(group);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException("新建组失败, 可能组名重复或过长", ex);
        } catch (Exception ex) {
            throw new RestException("新建组失败", ex);
        }
    }

    @Transactional
    public int modifyGroup(Long groupId, String name, String description) {
        try {
            AppGroup group = new AppGroup();
            group.setId(groupId);
            group.setName(name);
            group.setDescription(description);
            return appGroupDao.updateInfo (groupId, name, description);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException("修改组信息失败, 可能组名重复或过长", ex);
        } catch (Exception ex) {
            throw new RestException("修改组信息失败", ex);
        }
    }

    public Iterable<AppGroup> getAppGroups() {
        return appGroupDao.findAll();
    }

    public Iterable<AppGroup> getAppGroupsAndStat() {
        Iterable<AppGroup> groups = appGroupDao.findAll();
        groups.forEach(g -> {
            long ac = appDao.countInAppGroup(g.getId());
            g.setAppCount(ac);
            long uc = userDao.countInAppGroup(g.getId());
            g.setUserCount(uc);
            long hc = hostDao.count(g.getId());
            g.setHostCount(hc);
        });
        return groups;
    }

    public AppGroup getAppGroupById(long id) {
        return appGroupDao.findById(id).orElseThrow(() -> new RestException("组不存在"));
    }

    public Iterable<AppGroup> getUserGroups(long userId) {
        return appGroupDao.findByUserId(userId);
    }

    @Transactional
    public void deleteAppGroup(long id) {
        appGroupDao.deleteById(id);
    }

    @Transactional
    public void addHost(long groupId, long hostId) {
        Optional<Host> hostOp = hostDao.findById(hostId);
        if (!hostOp.isPresent()) {
            throw new RestException("主机不存在");
        }
        Host host = hostOp.get();
        if (host.getAppGroup() != null) {
            throw new ServiceException("主机已分配给分组："+host.getAppGroup().getName());
        }
        AppGroup g = new AppGroup();
        g.setId(groupId);
        host.setAppGroup(g);
        hostDao.save(host);
    }

    @Transactional
    public void removeHost(long groupId, long hostId) {
        Optional<Host> hostOp = hostDao.findById(hostId);
        if (!hostOp.isPresent()) {
            throw new RestException("主机不存在");
        }
        Host host = hostOp.get();
        if (host.getAppGroup() == null) {
            return;
        }
        if (host.getAppGroup().getId() != groupId) {
            throw new ServiceException("主机不属于指定分组");
        }
        host.setAppGroup(null);
        hostDao.save(host);
    }

    @Transactional
    public void addMember(long groupId, long userId) {
        if (appGroupDao.userInGroup(groupId, userId) == 0) {
            appGroupDao.addUserToGroup(groupId, userId);
        }
    }

    @Transactional
    public void removeMember(long groupId, long userId) {
        appGroupDao.removeUserFromGroup(groupId, userId);
    }

    @Transactional
    public void addApp(long groupId, long appId) {
        Optional<App> appOp = appDao.findById(appId);
        if (!appOp.isPresent()) {
            throw new RestException("应用不存在");
        }
        App app = appOp.get();
        if (app.getAppGroup() != null) {
            throw new ServiceException("应用已属于分组："+app.getAppGroup().getName());
        }
        AppGroup g = new AppGroup();
        g.setId(groupId);
        app.setAppGroup(g);
        appDao.save(app);
    }

    @Transactional
    public void removeApp(long groupId, long appId) {
        Optional<App> appOp = appDao.findById(appId);
        if (!appOp.isPresent()) {
            throw new RestException("应用不存在");
        }
        App app = appOp.get();
        if (app.getAppGroup() == null) {
            return;
        }
        if (app.getAppGroup().getId() != groupId) {
            throw new ServiceException("应用不属于指定分组");
        }
        app.setAppGroup(null);
        appDao.save(app);
    }

    @Transactional
    public void updateUserGroups(long userId, List<Long> groupIdList) {
        Set<AppGroup> oldGroups = appGroupDao.findByUserId(userId);
        List<Long> old = oldGroups.stream().map(AppGroup::getId).collect(Collectors.toList());
        for (Long id : groupIdList) {
            if (!old.contains(id)) {
                appGroupDao.addUserToGroup(id, userId);
            }
        }
        for (Long gid: old) {
            if (!groupIdList.contains(gid)) {
                appGroupDao.removeUserFromGroup(gid, userId);
            }
        }
    }
}
