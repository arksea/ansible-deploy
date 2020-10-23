package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.AppType;
import net.arksea.restapi.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Create by xiaohaixing on 2020/8/21
 */
@Component
public class AppTypesService {

    @Autowired
    AppTypeDao appTypeDao;

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
    public void saveAppType(AppType type) {
        try {
            AppType t = appTypeDao.save(type);
        } catch (Exception ex) {
            throw new RestException("保存应用类型失败", ex);
        }
    }

}
