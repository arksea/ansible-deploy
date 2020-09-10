package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.dao.HostDao;
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
            return appGroupDao.findAllByEnabled(true);
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
            AppGroup g = host.getAppGroup();
            if (g != null && g.getId() != null) {
                throw new ServiceException("主机已分配给分组："+g.getName());
            }
            g = new AppGroup();
            g.setId(groupId);
            host.setAppGroup(g);
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
            AppGroup g = host.getAppGroup();
            if (g == null) {
                return;
            }
            if (g.getId() != groupId) {
                throw new ServiceException("主机不属于指定分组");
            }
            host.setAppGroup(null);
            hostDao.save(host);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("向分组添加主机失败", ex);
        }
    }
}
