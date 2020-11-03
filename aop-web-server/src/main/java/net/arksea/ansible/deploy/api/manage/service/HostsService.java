package net.arksea.ansible.deploy.api.manage.service;

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
public class HostsService {

    @Autowired
    HostDao hostDao;


    @Transactional
    public Host saveHost(Host host) {
        try {
            return hostDao.save(host);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException("新增主机失败, 可能IP地址重复", ex);
        } catch (Exception ex) {
            throw new RestException("新增主机失败", ex);
        }
    }

    public Iterable<Host> getHosts() {
        try {
            return hostDao.findAllOrderByPrivateIp();
        } catch (Exception ex) {
            throw new RestException("查询主机列表失败", ex);
        }
    }

    @Transactional
    public void deleteHost(long id) {
        try {
            hostDao.deleteById(id);
        } catch (Exception ex) {
            throw new RestException("删除主机失败", ex);
        }
    }

    public Iterable<Host> getNotInGroup() {
        return hostDao.findByAppGroupId(null);
    }

    public Iterable<Host> getInGroup(Long groupId) {
        return hostDao.findByAppGroupId(groupId);
    }

    public Host findOne(long id) {
        return hostDao.findOne(id);
    }
}
