package net.arksea.ansible.deploy.api.manage.service;

import net.arksea.ansible.deploy.api.manage.entity.AppType;
import org.springframework.data.repository.CrudRepository;

/**
 * Create by xiaohaixing on 2020/9/24
 */
interface AppTypeDao extends CrudRepository<AppType, Long> {
    AppType findByName(String name);
}
