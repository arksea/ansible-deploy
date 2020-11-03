package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Entity
@Table(name = "dp2_port_type")
public class PortType extends IdEntity{
    private String name;
    private String description;
    private Set<Port> ports;
    private PortsStat stat;

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

    @JsonIgnore
    @OneToMany(mappedBy = "typeId",fetch = FetchType.LAZY)
    public Set<Port> getPorts() {
        return ports;
    }

    public void setPorts(Set<Port> ports) {
        this.ports = ports;
    }

    @OneToOne(mappedBy="portType", cascade = {CascadeType.REMOVE})
    public PortsStat getStat() {
        return stat;
    }

    public void setStat(PortsStat stat) {
        this.stat = stat;
    }
}
