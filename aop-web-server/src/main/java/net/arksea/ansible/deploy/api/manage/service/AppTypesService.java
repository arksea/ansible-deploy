package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.dao.AppVarDefineDao;
import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;
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
            Set<AppVarDefine> oldSet = type.getAppVarDefines();
            if (type.getId() == null && oldSet.size() > 0) {
                type.setAppVarDefines(new HashSet<>());
                AppType saved = appTypeDao.save(type);
                for (AppVarDefine d: oldSet) {
                    d.setAppTypeId(saved.getId());
                }
                Set<AppVarDefine> newSet = new HashSet<>();
                appVarDefineDao.save(oldSet).forEach(newSet::add);
                saved.setAppVarDefines(newSet);
                return saved;
            } else {
                return appTypeDao.save(type);
            }
        } catch (Exception ex) {
            throw new RestException("保存应用类型失败", ex);
        }
    }

}
