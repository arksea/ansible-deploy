package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.ansible.deploy.api.manage.msg.GetHosts;
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
import java.util.ArrayList;
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
                if (StringUtils.isNotBlank(msg.ipSearch)) {
                    predicateList.add(cb.like(root.get("privateIp"), "%" + msg.ipSearch + "%"));
                }
                if (msg.groupId != null) {
                    predicateList.add(cb.equal(root.get("appGroup").get("id"), msg.groupId));
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
            hostDao.delete(id);
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
