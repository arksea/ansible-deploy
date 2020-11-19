package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.auth.entity.User;
import net.arksea.ansible.deploy.api.manage.entity.App;
import net.arksea.ansible.deploy.api.manage.entity.AppOperation;
import net.arksea.ansible.deploy.api.manage.entity.AppOperationType;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.ansible.deploy.api.manage.msg.GetHosts;
import net.arksea.ansible.deploy.api.manage.msg.GetOperationJobHistory;
import net.arksea.ansible.deploy.api.manage.msg.GetUserApps;
import net.arksea.ansible.deploy.api.operator.entity.OperationJob;
import net.arksea.restapi.RestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public GetHosts.Response findHosts(GetHosts.Request msg) {
        int page = msg.page < 1 ? 0 : msg.page - 1;
        Pageable pageable = new PageRequest(page, msg.pageSize, Sort.Direction.ASC, "privateIp");
        Specification<Host> specification = new Specification<Host>() {
            @Override
            public Predicate toPredicate(Root<Host> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                if (msg.groupId == null || msg.groupId > 0) {
                    predicateList.add(cb.equal(root.get("appGroupIp").as(Long.class), msg.groupId));
                }
                if (StringUtils.isNotBlank(msg.ipSearch)) {
                    predicateList.add(cb.like(root.get("privateIp"), "%" + msg.ipSearch + "%"));
                }
                return cb.and(predicateList.toArray(new Predicate[0]));
            }
        };
        Page<Host> hosts = hostDao.findAll(specification, pageable);
        return new GetHosts.Response(hosts.getTotalElements(),hosts.getTotalPages(), hosts.getContent());
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
