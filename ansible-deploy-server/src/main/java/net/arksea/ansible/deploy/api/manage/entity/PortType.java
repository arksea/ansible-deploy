package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private int allCount;
    private int restCount;

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
    @Fetch(FetchMode.SELECT)
    public Set<Port> getPorts() {
        return ports;
    }

    public void setPorts(Set<Port> ports) {
        this.ports = ports;
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
