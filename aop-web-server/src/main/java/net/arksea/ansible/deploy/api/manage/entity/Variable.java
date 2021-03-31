package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;

/**
 *
 * @author xiaohaixing
 */
@MappedSuperclass
public abstract class Variable extends IdEntity {

    private String name;// 变量名
    private String value;// 变量值
    private Boolean isPort;// 是否端口值，用于主机范围的唯一性判断
    private boolean deleted; //当此类型变量定义删除时，将被级联标记为true,留着变量的值用于误删恢复

    @Column(length = 24, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Column(length = 256, nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    public Boolean getIsPort() {
        return isPort;
    }

    public void setIsPort(final Boolean isPort) {
        this.isPort = isPort;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
