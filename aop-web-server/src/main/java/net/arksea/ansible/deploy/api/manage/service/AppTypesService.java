package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppVarDefineDao;
import net.arksea.ansible.deploy.api.manage.dao.VersionVarDefineDao;
import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;
import net.arksea.ansible.deploy.api.manage.entity.VersionVarDefine;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class AppTypesService {

    @Autowired
    AppTypeDao appTypeDao;
    @Autowired
    AppVarDefineDao appVarDefineDao;
    @Autowired
    VersionVarDefineDao verVarDefineDao;

    public AppType findOne(long id) {
        try {
            return appTypeDao.findOne(id);
        } catch (Exception ex) {
            throw new RestException("查询应用类型失败", ex);
        }
    }

    public Iterable<AppType> findAll() {
        try {
            return appTypeDao.findAll();
        } catch (Exception ex) {
            throw new RestException("查询应用类型失败", ex);
        }
    }

    @Transactional
    public void deleteAppType(long id) {
        try {
            appTypeDao.delete(id);
        } catch (Exception ex) {
            throw new RestException("删除应用类型失败", ex);
        }
    }

    @Transactional
    public AppType saveAppType(AppType type) {
        try {
            Set<AppVarDefine> newAppVars = type.getAppVarDefines();
            Set<VersionVarDefine> newVerVars = type.getVersionVarDefines();
            if (type.getId() == null) {
                type.setAppVarDefines(new HashSet<>());
                type.setVersionVarDefines(new HashSet<>());
                AppType saved = appTypeDao.save(type);
                newAppVars.forEach(v -> v.setAppTypeId(saved.getId()));
                newVerVars.forEach(v -> v.setAppTypeId(saved.getId()));
                Set<AppVarDefine> savedAppVars = new HashSet<>();
                Set<VersionVarDefine> savedVerVars = new HashSet<>();
                appVarDefineDao.save(newAppVars).forEach(savedAppVars::add);
                verVarDefineDao.save(newVerVars).forEach(savedVerVars::add);
                saved.setAppVarDefines(savedAppVars);
                saved.setVersionVarDefines(savedVerVars);
                return saved;
            } else {
                return appTypeDao.save(type);
            }
        } catch (Exception ex) {
            throw new RestException("保存应用类型失败", ex);
        }
    }

    private void updateVariables() {

    }

}
