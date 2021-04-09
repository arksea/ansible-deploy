package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppCustomOperationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface AppCustomOperationCodeDao extends CrudRepository<AppCustomOperationCode, Long> {
    List<AppCustomOperationCode> findByAppId(long appId);
}
