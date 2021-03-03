package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.manage.dao.AppDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionVarDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionVarDefineDao;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Create by xiaohaixing on 2020/9/14
 */
@Component
public class VersionService {

    @Autowired
    VersionDao versionDao;
    @Autowired
    VersionVarDao versionVarDao;
    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    VersionVarDefineDao versionVarDefineDao;
    @Autowired
    PortDao portDao;
    @Autowired
    PortTypeDao portTypeDao;
    @Autowired
    AppDao appDao;
    @Autowired
    PortsService portsService;

    public Version findOne(final Long id) {
        return versionDao.findOne(id);
    }

    @Transactional
    public void addHosts(long versionId, List<Long> hosts) {
        for (Long h: hosts) {
            versionDao.addHost(versionId, h);
        }
    }

    @Transactional
    public void deleteById(long versionId) {
        Version ver = versionDao.findOne(versionId);
        //释放占用的端口
        for (VersionVariable var: ver.getVars()) {
            if (var.getIsPort()) {
                List<Port> list = portDao.findByValue(Integer.parseInt(var.getValue()));
                if (list.size() > 0) {
                    Port port = list.get(0);
                    portTypeDao.incRestCount(1, port.getTypeId());
                    portDao.releasePortByValue(port.getValue());
                }
            }
        }
        versionDao.delete(versionId);
    }

    @Transactional
    public void removeHostFromVersion(long versionId, long hostId) {
        versionDao.removeHost(versionId, hostId);
    }

    @Transactional
    public Version saveVersion(final Version ver) {
        final boolean isNewAction = ver.getId() == null;
        if (isNewAction) {
            Version v = versionDao.findByAppIdAndName(ver.getAppId(), ver.getName());
            if (v != null) {
                throw new RestException("版本名重复");
            }
        }
        try {
            App app = appDao.findOne(ver.getAppId());
            if (!isNewAction) {
                Version old = versionDao.findOne(ver.getId());
                portsService.updatePortVariables(app.getId(), old.getVars(), ver.getVars());
            }
            Version saved = versionDao.save(ver);
            if (isNewAction) {//分配端口
                initPortVariables(app.getId(), app.getAppType().getId(), saved.getVars(), VersionVariable::new);
            }
            long id = saved.getId();
            for (final VersionVariable v : ver.getVars()) {
                v.setVersionId(id);
                versionVarDao.save(v);
            }
            return saved;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("保存版本失败", ex);
        }
    }

    @Transactional
    private <T extends Variable> void initPortVariables(long appId, long appTypeId, Set<T> vars, Supplier<T> varCreator) {
        List<VersionVarDefine> defines = versionVarDefineDao.findByAppTypeId(appTypeId);
        for (VersionVarDefine def: defines) {
            if (def.getPortType() != null) {
                PortType portType = def.getPortType();
                List<Port> free = portDao.getOneFreeByTypeId(portType.getId());
                if (free.size() == 0) {
                    throw new ServiceException("'"+portType.getName()+"'端口可用数不够，请联系管理员");
                }
                Port p = free.get(0);
                int n = portDao.holdPortByValue(p.getValue(), appId);
                if (n == 0) {
                    throw new ServiceException("端口 "+p.getValue()+" 已被占用，请稍后重试");
                } else {
                    T var = varCreator.get();
                    var.setIsPort(true);
                    var.setName(def.getName());
                    var.setValue(Integer.toString(p.getValue()));
                    vars.add(var);
                    portTypeDao.incRestCount(-1, portType.getId());
                }
            }
        }
    }

    public Version createVersionTemplate(String appTypeName) {
        AppType type = appTypeDao.findByName(appTypeName);
        Version ver = new Version();
        ver.setExecOpt("");
        ver.setName("");
        ver.setRepository("trunk");
        ver.setRevision("HEAD");
        ver.setTargetHosts(new HashSet<>());
        ver.setVars(new HashSet<>());
        Iterable<VersionVarDefine> defines = versionVarDefineDao.findByAppTypeId(type.getId());
        for (VersionVarDefine def : defines) {
            if (def.getPortType() == null) { //端口类型变量保存时自动分配，无需返回给客户端进行编辑
                VersionVariable var = new VersionVariable();
                var.setValue(def.getDefaultValue());
                var.setName(def.getName());
                var.setIsPort(def.getPortType() != null);
                ver.getVars().add(var);
            }
        }
        return ver;
    }
}
