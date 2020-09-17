package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/16
 */
@Entity
@Table(name = "dp2_port_section")
public class PortSection extends IdEntity {
    private int typeId;
    private int minValue; //min =< port.value =< max
    private int maxValue;

    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
}
