package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.ServiceException;
import net.arksea.ansible.deploy.api.manage.dao.*;
import net.arksea.ansible.deploy.api.manage.entity.*;
import net.arksea.restapi.RestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class AppTypesService {

    private static final Logger logger = LogManager.getLogger(AppTypesService.class);
    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    AppVarDefineDao appVarDefineDao;
    @Autowired
    VersionVarDefineDao verVarDefineDao;
    @Autowired
    VersionVarDao versionVarDao;
    @Autowired
    VersionDao versionDao;
    @Autowired
    AppVarDao appVarDao;
    @Autowired
    AppDao appDao;
    @Autowired
    PortsService portsService;

    public AppType findOne(long id) {
        Optional<AppType> op = appTypeDao.findById(id);
        if (op.isPresent()) {
            return op.get();
        } else {
            throw new ServiceException("AppType not found");
        }
    }

    public Iterable<AppType> findAll() {
        return appTypeDao.findAll();
    }

    @Transactional
    public void deleteAppType(long id) {
        appTypeDao.deleteById(id);
    }

    @Transactional
    public AppType saveAppType(AppType type) {
        try {
            if (type.getId() == null) {
                Set<AppVarDefine> newAppVars = type.getAppVarDefines();
                Set<VersionVarDefine> newVerVars = type.getVersionVarDefines();
                type.setAppVarDefines(new HashSet<>());
                type.setVersionVarDefines(new HashSet<>());
                AppType saved = appTypeDao.save(type);
                newAppVars.forEach(v -> v.setAppTypeId(saved.getId()));
                newVerVars.forEach(v -> v.setAppTypeId(saved.getId()));
                Set<AppVarDefine> savedAppVars = new HashSet<>();
                Set<VersionVarDefine> savedVerVars = new HashSet<>();
                appVarDefineDao.saveAll(newAppVars).forEach(savedAppVars::add);
                verVarDefineDao.saveAll(newVerVars).forEach(savedVerVars::add);
                saved.setAppVarDefines(savedAppVars);
                saved.setVersionVarDefines(savedVerVars);
                return saved;
            } else {
                Optional<AppType> oldTypeOp = appTypeDao.findById(type.getId());
                if (oldTypeOp.isPresent()) {
                    //必须新建一个Set实例，否则持久对象会在保存后被Hibernate修改
                    AppType oldType = oldTypeOp.get();
                    Set<VersionVarDefine> oldVerVars = new HashSet<>(oldType.getVersionVarDefines());
                    Set<AppVarDefine> oldAppVars = new HashSet<>(oldType.getAppVarDefines());
                    AppType saved = appTypeDao.save(type);
                    updateAppVars(oldType.getId(), oldAppVars, saved.getAppVarDefines());
                    updateVerVars(oldType.getId(), oldVerVars, saved.getVersionVarDefines());
                    return saved;
                } else {
                    throw new ServiceException("AppType not found");
                }
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RestException("保存应用类型失败", ex);
        }
    }

    @Transactional
    private void updateAppVars(long appTypeId, Set<AppVarDefine> oldAppVars, Set<AppVarDefine> newAppVars) {
        List<AppVarDefine> addAppVars = new LinkedList<>();
        newAppVars.forEach(d -> {
            if (!oldAppVars.contains(d)) {
                addAppVars.add(d);
            }
        });
        List<AppVarDefine> delAppVars = new LinkedList<>();
        oldAppVars.forEach(d -> {
            if (!newAppVars.contains(d)) {
                delAppVars.add(d);
            }
        });
        List<App> apps = appDao.findByAppType(appTypeId);
        //新增变量
        for (AppVarDefine def: addAppVars) {
            for (App app: apps) {
                AppVariable exists = appVarDao.findByAppIdAndName(app.getId(), def.getName());
                if (exists == null) {
                    AppVariable v = new AppVariable();
                    v.setAppId(app.getId());
                    v.setIsPort(def.getPortType() != null);
                    v.setName(def.getName());
                    if (def.getPortType() != null) {
                        portsService.initPortVariable(app.getId(), v, def);
                    } else {
                        v.setValue(def.getDefaultValue());
                    }
                    appVarDao.save(v);
                } else if (exists.isDeleted()) {
                    exists.setDeleted(false);
                    appVarDao.save(exists);
                } else {
                    logger.warn("App变量状态异常(未被标记为删除状态)：id={},name={}",exists.getId(),exists.getName());
                }
            }
        }
        //删除变量（标记）
        for (AppVarDefine def: delAppVars) {
            for (App app: apps) {
                AppVariable exists = appVarDao.findByAppIdAndName(app.getId(), def.getName());
                if (exists == null) {
                    logger.warn("App变量状态异常(不存在)：appId={},name={}",app.getId(),def.getName());
                } else if (exists.isDeleted()) {
                    logger.warn("App变量状态异常(被标记为删除状态)：id={},name={}",exists.getId(),exists.getName());
                } else {
                    exists.setDeleted(true);
                    appVarDao.save(exists);
                }
            }
        }
    }

    @Transactional
    private void updateVerVars(long appTypeId, Set<VersionVarDefine> oldVerVars, Set<VersionVarDefine> newVerVars) {
        logger.debug("oldVerVars={}", oldVerVars.stream()
                .map(VersionVarDefine::getName)
                .reduce("",(s,v) -> s + v + ";"));
        logger.debug("newVerVars={}", newVerVars.stream()
                .map(VersionVarDefine::getName)
                .reduce("",(s,v) -> s + v + ";"));
        List<VersionVarDefine> addVerVars = new LinkedList<>();
        newVerVars.forEach(d -> {
            if (!oldVerVars.contains(d)) {
                addVerVars.add(d);
            }
        });
        logger.debug("addVerVars={}", addVerVars.stream()
                .map(VersionVarDefine::getName)
                .reduce("",(s,v) -> s + v + ";"));
        List<VersionVarDefine> delVerVars = new LinkedList<>();
        oldVerVars.forEach(d -> {
            if (!newVerVars.contains(d)) {
                delVerVars.add(d);
            }
        });
        logger.debug("delVerVars={}", delVerVars.stream()
                .map(VersionVarDefine::getName)
                .reduce("",(s,v) -> s + v + ";"));
        List<Version> versions = versionDao.findByAppType(appTypeId);
        logger.debug("The type versions={}", versions.stream()
                .map(Version::getName)
                .reduce("", (s,v) -> s + v + ";"));
        //新增变量
        for (VersionVarDefine def: addVerVars) {
            for (Version ver: versions) {
                VersionVariable exists = versionVarDao.findByVersionIdAndName(ver.getId(), def.getName());
                if (exists == null) {
                    logger.debug("ver '{}' add version var: '{}'", ver.getName(), def.getName());
                    VersionVariable v = new VersionVariable();
                    v.setVersionId(ver.getId());
                    v.setIsPort(def.getPortType() != null);
                    v.setName(def.getName());
                    if (def.getPortType() != null) {
                        portsService.initPortVariable(ver.getAppId(), v, def);
                    } else {
                        v.setValue(def.getDefaultValue());
                    }
                    versionVarDao.save(v);
                } else if (exists.isDeleted()) {
                    logger.debug("ver '{}' version var: '{}' set deleted=false", ver.getName(), def.getName());
                    exists.setDeleted(false);
                    versionVarDao.save(exists);
                } else {
                    logger.warn("Version变量状态异常(未被标记为删除状态)：id={},name={}",exists.getId(),exists.getName());
                }
            }
        }
        //删除变量（标记）
        for (VersionVarDefine def: delVerVars) {
            for (Version ver: versions) {
                VersionVariable exists = versionVarDao.findByVersionIdAndName(ver.getId(), def.getName());
                if (exists == null) {
                    logger.warn("Version变量状态异常(不存在)：versionId={},name={}", ver.getId(), def.getName());
                } else if (exists.isDeleted()) {
                    logger.warn("Version变量状态异常(被标记为删除状态)：id={},name={}",exists.getId(),exists.getName());
                } else {
                    logger.debug("ver '{}' version var: '{}' set deleted=true", ver.getName(), def.getName());
                    exists.setDeleted(true);
                    versionVarDao.save(exists);
                }
            }
        }
    }
}
