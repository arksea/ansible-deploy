package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.VersionVarDefine;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
public interface VersionVarDefineDao extends CrudRepository<VersionVarDefine, Long> {
    List<VersionVarDefine> findByAppTypeId(long appTypeId);
    int deleteByAppTypeId(long appTypeId);
}
