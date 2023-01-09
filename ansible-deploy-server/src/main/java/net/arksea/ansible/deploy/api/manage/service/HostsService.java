package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.manage.dao.AppGroupDao;
import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import net.arksea.ansible.deploy.api.manage.entity.Host;
import net.arksea.ansible.deploy.api.manage.msg.GetHosts;
import net.arksea.restapi.RestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class HostsService {

    @Autowired
    HostDao hostDao;
    @Autowired
    AppGroupDao appGroupDao;

    @Transactional
    public Host saveHost(Host host) {
        try {
            return hostDao.save(host);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException("新增主机失败, 可能IP地址重复", ex);
        }
    }

    @Transactional
    public Iterable<Host> batchAddHosts(String ipRange, String descPrefix, long groupId) {
        final String formatError = "IP范围格式错误，只支持最后一位的范围表示法";
        final String[] strs = StringUtils.split(ipRange, "~.");
        // 0   1   2   3   4
        //192.168.101.100~110
        if (strs.length != 5) {
            throw new ServiceException(formatError);
        }
        String ipPre = strs[0]+'.'+strs[1]+'.'+strs[2]+'.';
        int min = Integer.parseUnsignedInt(strs[3]);
        int max = Integer.parseUnsignedInt(strs[4]);
        //ServiceException.logger.info("====min={},max={},ipPre={}",min,max,ipPre);
        if (min<1 || max>255) {
            throw new ServiceException(formatError);
        }
        Optional<AppGroup> op = appGroupDao.findById(groupId);
        if (!op.isPresent()) {
            throw new ServiceException("组不存在");
        }
        AppGroup g = op.get();
        List<Host> hosts = new LinkedList<>();
        for (int n=min;n<=max;++n) {
            String ip = ipPre + n;
            if (!hostDao.existsByPrivateIp(ip)) {
                Host h = new Host();
                h.setPrivateIp(ip);
                h.setAppGroup(g);
                h.setDescription(descPrefix + n);
                h.setEnabled(true);
                hosts.add(h);
            }
        }
        return hostDao.saveAll(hosts);
    }

//    public GetHosts.Response findHosts(GetHosts.Request msg) {
//        int page = msg.page < 1 ? 0 : msg.page - 1;
//        Pageable pageable = new PageRequest(page, msg.pageSize, Sort.Direction.ASC, "inet_aton(privateIp)");
//        Specification<Host> specification = new Specification<Host>() {
//            @Override
//            public Predicate toPredicate(Root<Host> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                List<Predicate> predicateList = new ArrayList<>();
//                if (StringUtils.isNotBlank(msg.ipSearch)) {
//                    predicateList.add(cb.like(root.get("privateIp"), "%" + msg.ipSearch + "%"));
//                }
//                if (msg.groupId != null) {
//                    predicateList.add(cb.equal(root.get("appGroup").get("id"), msg.groupId));
//                }
//                return cb.and(predicateList.toArray(new Predicate[0]));
//            }
//        };
//        Page<Host> hosts = hostDao.findAll(specification, pageable);
//        return new GetHosts.Response(hosts.getTotalElements(),hosts.getTotalPages(), hosts.getContent());
//    }

    public GetHosts.Response findHosts(GetHosts.Request msg) {
        int offset = (msg.page < 1 ? 0 : msg.page - 1) * msg.pageSize;
        List<Host> hosts;
        long total;
        if (StringUtils.isNotBlank(msg.ipSearch) && msg.groupId != null) {
            String like = "%" + msg.ipSearch + "%";
            hosts = hostDao.findPage(offset, msg.pageSize, msg.groupId, like);
            total = hostDao.count(msg.groupId, msg.ipSearch);
        } else if (StringUtils.isNotBlank(msg.ipSearch)) {
            String like = "%" + msg.ipSearch + "%";
            hosts = hostDao.findPage(offset, msg.pageSize, like);
            total = hostDao.count(msg.ipSearch);
        } else if (msg.groupId != null) {
            hosts = hostDao.findPage(offset, msg.pageSize, msg.groupId);
            total = hostDao.count(msg.groupId);
        } else {
            hosts = hostDao.findPage(offset, msg.pageSize);
            total = hostDao.count();
        }
        long totalPages = total/msg.pageSize;
        if (total % msg.pageSize > 0) {
            totalPages++;
        }
        return new GetHosts.Response(total, totalPages, hosts);
    }

    @Transactional
    public void deleteHost(long id) {
        hostDao.deleteById(id);
    }

    public Iterable<Host> getNotInGroup() {
        return hostDao.findByAppGroupId(null);
    }

    public Iterable<Host> getInGroup(Long groupId) {
        return hostDao.findByAppGroupId(groupId);
    }

    public Host findOne(long id) {
        Optional<Host> op = hostDao.findById(id);
        if (op.isPresent()) {
            return op.get();
        } else {
            throw new ServiceException("主机不存在");
        }
    }
}
