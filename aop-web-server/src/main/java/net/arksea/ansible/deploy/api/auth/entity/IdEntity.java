package net.arksea.ansible.deploy.api.auth.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义id的entity基类.
 */
@MappedSuperclass
public class IdEntity {
    protected Long id;

    protected IdEntity() {
        //仅作为基类，不创建实例
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
