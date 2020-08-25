package net.arksea.ansible.deploy.api.manage.dao;

import net.arksea.ansible.deploy.api.manage.entity.AppGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppGroupDao extends CrudRepository<AppGroup, Long> {
    @Modifying
    @Query("update AppGroup g set g.enabled = false where g.id = ?1")
    void deleteById(long id);

    Iterable<AppGroup> findAllByEnabled(boolean enabled);
}
