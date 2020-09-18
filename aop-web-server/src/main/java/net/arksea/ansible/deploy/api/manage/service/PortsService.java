package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.PortDao;
import net.arksea.ansible.deploy.api.manage.dao.PortSectionDao;
import net.arksea.ansible.deploy.api.manage.dao.PortTypeDao;
import net.arksea.ansible.deploy.api.manage.dao.PortsStatDao;
import net.arksea.ansible.deploy.api.manage.entity.Port;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import net.arksea.ansible.deploy.api.manage.entity.PortsStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Component
public class PortsService {

    @Autowired
    PortSectionDao portSectionDao;

    @Autowired
    PortDao portDao;

    @Autowired
    PortTypeDao portTypeDao;

    @Autowired
    PortsStatDao portsStatDao;

    @Transactional
    public PortSection addPortSection(PortSection s) {
        if (s.getId() != null) {
            throw new RuntimeException("端口区间分配后不能修改");
        }
        if (portDao.countByRange(s.getMinValue(), s.getMaxValue()) > 0) {
            throw new RuntimeException("此端口区间包含部分已分配端口");
        }
        for (int i=s.getMinValue(); i<=s.getMaxValue(); ++i) {
            Port p = new Port();
            p.setEnabled(true);
            p.setTypeId(s.getType().getId());
            p.setValue(i);
            portDao.save(p);
        }
        int typeId = s.getType().getId();
        PortsStat stat = portsStatDao.findByTypeId(typeId);
        if (stat == null) {
            stat = new PortsStat();
            stat.setTypeId(typeId);
            stat.setAllCount(0);
            stat.setRestCount(0);
        }
        int count = s.getMaxValue() - s.getMinValue() + 1;
        stat.setAllCount(stat.getAllCount() + count);
        stat.setRestCount(stat.getRestCount() + count);
        portsStatDao.save(stat);

        List<PortSection> sections = portSectionDao.findByTypeId(typeId);
        PortSection left = null;
        PortSection right = null;
        for (PortSection old : sections) { //合并连续的区间
            if (old.getMinValue() == s.getMaxValue()+1) {
                right = old;
            } else if (old.getMaxValue() == s.getMinValue()-1) {
                left = old;
            }
        }
        if (right == null && left == null) {
            return portSectionDao.save(s);
        } else if (left == null) {
            right.setMinValue(s.getMinValue());
            return portSectionDao.save(right);
        } else if (right == null) {
            left.setMaxValue(s.getMaxValue());
            return portSectionDao.save(left);
        } else {
            left.setMaxValue(right.getMaxValue());
            portSectionDao.delete(right.getId());
            return portSectionDao.save(left);
        }
    }

    public Iterable<PortType> getPortTypes() {
        return portTypeDao.findAll();
    }
    public Iterable<PortSection> getPortSections() {
        return portSectionDao.findAll();
    }

}
