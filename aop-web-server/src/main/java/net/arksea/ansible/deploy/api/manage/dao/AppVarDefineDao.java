package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppVarDefine;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface AppVarDefineDao extends CrudRepository<AppVarDefine, Long> {
    List<AppVarDefine> findByAppTypeId(long appTypeId);
}
