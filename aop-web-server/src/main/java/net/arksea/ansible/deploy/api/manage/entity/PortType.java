package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Entity
@Table(name = "dp2_port_type")
public class PortType {
    private int id;
    private String name;
    private String description;
    private List<PortSection> sections;

    @Id
    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(length = 32, nullable = false)
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

    @OneToMany(mappedBy = "typeId", fetch = FetchType.EAGER)
    @OrderBy("minValue")
    public List<PortSection> getSections() {
        return sections;
    }

    public void setSections(List<PortSection> sections) {
        this.sections = sections;
    }
}
