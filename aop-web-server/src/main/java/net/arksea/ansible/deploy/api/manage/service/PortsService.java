package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.PortDao;
import net.arksea.ansible.deploy.api.manage.dao.PortSectionDao;
import net.arksea.ansible.deploy.api.manage.dao.PortTypeDao;
import net.arksea.ansible.deploy.api.manage.entity.Port;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

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

    @Transactional
    public PortSection addPortSection(PortSection portSection) {
        if (portSection.getId() != null) {
            throw new RuntimeException("端口可用范围分配后不能修改");
        }
        PortSection s = portSectionDao.save(portSection);
        for (int i=s.getMinValue(); i<=s.getMaxValue(); ++i) {
            Port p = new Port();
            p.setEnabled(true);
            p.setSectionId(s.getId());
            p.setTypeId(s.getTypeId());
            p.setValue(i);
            portDao.save(p);
        }
        return s;
    }

    public Iterable<PortType> getPortTypes() {
        return portTypeDao.findAll();
    }

}
