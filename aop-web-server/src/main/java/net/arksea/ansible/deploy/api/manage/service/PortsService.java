package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.PortSectionDao;
import net.arksea.ansible.deploy.api.manage.dao.PortTypeDao;
import net.arksea.ansible.deploy.api.manage.dao.PortsStatDao;
import net.arksea.ansible.deploy.api.manage.entity.Port;
import net.arksea.ansible.deploy.api.manage.entity.PortSection;
import net.arksea.ansible.deploy.api.manage.entity.PortType;
import net.arksea.ansible.deploy.api.manage.entity.PortsStat;
import net.arksea.restapi.RestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Component
public class PortsService {
    private static Logger logger = LogManager.getLogger(PortsService.class);
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
            throw new RuntimeException("修改端口区间有专用的方法，不能调用添加方法");
        }
        if (portDao.countByRange(s.getMinValue(), s.getMaxValue()) > 0) {
            throw new RuntimeException("要分配的区间与现有区间重叠");
        }
        //添加端口
        for (int i=s.getMinValue(); i<=s.getMaxValue(); ++i) {
            Port p = new Port();
            p.setEnabled(true);
            p.setTypeId(s.getType().getId());
            p.setValue(i);
            portDao.save(p);
        }
        //修改统计
        int count = s.getMaxValue() - s.getMinValue() + 1;
        int typeId = s.getType().getId();
        PortsStat stat = portsStatDao.findByTypeId(typeId);
        if (stat == null) {
            stat = new PortsStat();
            stat.setTypeId(typeId);
            stat.setAllCount(count);
            stat.setRestCount(count);
            portsStatDao.save(stat);
        } else {
            portsStatDao.incAllCount(count, typeId);
        }
        //判断是否合并连续区间
        List<PortSection> sections = portSectionDao.findByTypeId(typeId);
        PortSection left = null;
        PortSection right = null;
        for (PortSection old : sections) {
            if (old.getMinValue() == s.getMaxValue()+1) {
                right = old;
            } else if (old.getMaxValue() == s.getMinValue()-1) {
                left = old;
            }
        }
        if (right == null && left == null) { //左右皆无连接
            return portSectionDao.save(s);
        } else if (left == null) { //与右边连接
            right.setMinValue(s.getMinValue());
            return portSectionDao.save(right);
        } else if (right == null) {//与左边连接
            left.setMaxValue(s.getMaxValue());
            return portSectionDao.save(left);
        } else { //连接左右区间
            left.setMaxValue(right.getMaxValue());
            portSectionDao.delete(right.getId());
            return portSectionDao.save(left);
        }
    }

    @Transactional
    public PortSection modifyPortSection(PortSection s) {
        if (s.getId() == null) {
            throw new RuntimeException("添加端口区间有专用的方法，不能调用修改方法");
        }
        PortSection old = portSectionDao.findOne(s.getId());
        //判断是否冲突
        if (s.getMinValue() < old.getMinValue()) { //向左扩展
            int l = s.getMinValue();
            int r = old.getMinValue() - 1;
            if (portDao.countByRange(l, r) > 0) {
                throw new RuntimeException("要扩展的区间与其他区间重叠");
            }
        } else if (s.getMinValue() > old.getMinValue()) {//左边收缩
            int l = old.getMinValue();
            int r = s.getMinValue()-1;
            long count = portDao.countHasUsedByRange(l,r);
            if (count > 0) {
                throw new RuntimeException("要收缩的区间包含已使用端口");
            }
        }
        if (s.getMaxValue() > old.getMaxValue()) {//向右扩展
            int l = old.getMaxValue() + 1;
            int r = s.getMaxValue();
            if (portDao.countByRange(l, r) > 0) {
                throw new RuntimeException("要扩展的区间与其他区间重叠");
            }
        } else if (s.getMaxValue() < old.getMaxValue()) {//右边收缩
            int l = s.getMaxValue() + 1;
            int r = old.getMaxValue();
            long count = portDao.countHasUsedByRange(l,r);
            if (count > 0) {
                throw new RuntimeException("要收缩的区间包含已使用端口");
            }
        }
        //添加端口
        if (s.getMinValue() < old.getMinValue()) { //向左扩展
            int l = s.getMinValue();
            int r = old.getMinValue()-1;
            for (int i=l;i<=r;++i) {
                Port p = new Port();
                p.setEnabled(true);
                p.setTypeId(s.getType().getId());
                p.setValue(i);
                portDao.save(p);
            }
        } else if (s.getMinValue() > old.getMinValue()) {//左边收缩
            int l = old.getMinValue();
            int r = s.getMinValue()-1;
            portDao.deleteByRange(l,r);
        }
        if (s.getMaxValue() > old.getMaxValue()) {//向右扩展
            int l = old.getMaxValue()+1;
            int r = s.getMaxValue();
            for (int i=l;i<=r;++i) {
                Port p = new Port();
                p.setEnabled(true);
                p.setTypeId(s.getType().getId());
                p.setValue(i);
                portDao.save(p);
            }
        } else if (s.getMaxValue() < old.getMaxValue()) {//右边收缩
            int l = s.getMaxValue()+1;
            int r = old.getMaxValue();
            portDao.deleteByRange(l,r);
        }
        //修改统计
        int typeId = s.getType().getId();
        int count = s.getMaxValue() - s.getMinValue() - (old.getMaxValue() - old.getMinValue());
        portsStatDao.incAllCount(count, typeId);
        //判断是否合并连续区间
        List<PortSection> sections = portSectionDao.findByTypeId(typeId);
        PortSection left = null;
        PortSection right = null;
        for (PortSection o : sections) {
            if (o.getId().equals(s.getId())) {//不和自己比较
                continue;
            }
            if (o.getMinValue() == s.getMaxValue()+1) {
                right = o;
            } else if (o.getMaxValue() == s.getMinValue()-1) {
                left = o;
            }
        }
        if (right == null && left == null) { //左右皆无连接
            return portSectionDao.save(s);
        } else if (left == null) { //与右边连接
            s.setMaxValue(right.getMaxValue());
            portSectionDao.delete(right.getId());
            return portSectionDao.save(s);
        } else if (right == null) {//与左边连接
            s.setMinValue(left.getMinValue());
            portSectionDao.delete(left.getId());
            return portSectionDao.save(s);
        } else { //连接左右区间
            s.setMinValue(left.getMinValue());
            s.setMaxValue(right.getMaxValue());
            portSectionDao.delete(left.getId());
            portSectionDao.delete(right.getId());
            return portSectionDao.save(s);
        }
    }

    public Iterable<PortType> getPortTypes() {
        return portTypeDao.findAll();
    }
    public Iterable<PortSection> getPortSections() {
        return portSectionDao.findAll();
    }

    @Transactional
    public void deletePortSection(long id) {
        PortSection s = portSectionDao.findOne(id);
        long count = portDao.countHasUsedByRange(s.getMinValue(),s.getMaxValue());
        if (count > 0) {
            throw new RuntimeException("此端口区间包含"+count+"个已使用端口，不能删除");
        }
        portDao.deleteByRange(s.getMinValue(),s.getMaxValue());
        portSectionDao.delete(id);

        //修改统计
        int typeId = s.getType().getId();
        int all = s.getMaxValue() - s.getMinValue() + 1;
        portsStatDao.incAllCount(-all, typeId);
    }

    public List<Port> searchByPrefix(String prefix,int limit) {
        return portDao.searchByPrefix(prefix, Math.min(100, limit));
    }

    public List<Port> getByValue(int value) {
        return portDao.findByValue(value);
    }
}
