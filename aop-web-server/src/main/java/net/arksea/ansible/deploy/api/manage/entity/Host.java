package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 *
 * @author xiaohaixing
 */
@Entity
@Table(name="dp2_hosts")
public class Host extends IdEntity {

    private String publicIp;//公网IP
    private String privateIp;//内网IP
    private String description;//主机用途描述
    private Timestamp createTime; //创建时间

    @Column(name = "public_ip", length = 36)
    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(final String publicIp) {
        this.publicIp = publicIp;
    }
    
    @Column(name = "private_ip", length = 36, nullable = false, unique = true)
    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(final String privateIp) {
        this.privateIp = privateIp;
    }

    @Column(length = 64, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Column(nullable = false, columnDefinition = ("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"))
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return privateIp;
    }
}
