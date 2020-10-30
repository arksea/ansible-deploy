package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Entity
@Table(name = "dp2_port_type")
public class PortType {
    private int id;
    private String name;
    private String description;
    private PortsStat stat;

    @Id
    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(length = 32, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 128)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne
    @PrimaryKeyJoinColumn
    public PortsStat getStat() {
        return stat;
    }

    public void setStat(PortsStat stat) {
        this.stat = stat;
    }
}
