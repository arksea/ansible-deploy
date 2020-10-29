package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Entity
@Table(name = "dp2_ports_stat")
public class PortsStat {
    private int typeId;
    private int allCount;
    private int restCount;

    @Id
    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    public int getRestCount() {
        return restCount;
    }

    public void setRestCount(int restCount) {
        this.restCount = restCount;
    }
}
