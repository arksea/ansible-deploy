package net.arksea.ansible.deploy.api.manage.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/17
 */
@Entity
@Table(name = "dp2_ports_stat")
public class PortsStat {
    private Long typeId;
    private PortType portType;
    private int allCount;
    private int restCount;

    @Id
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @JsonIgnore
    @OneToOne
    @MapsId
    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
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
