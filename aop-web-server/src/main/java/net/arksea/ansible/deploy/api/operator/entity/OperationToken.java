package net.arksea.ansible.deploy.api.operator.entity;

import net.arksea.ansible.deploy.api.auth.entity.User;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by xiaohaixing on 2020/9/30
 */
@Entity
@Table(name = "dp2_operation_token")
public class OperationToken extends IdEntity {
    private Long appId;
    private Long jobId;
    private User holder;
    private Timestamp holdTime;
    private Timestamp releaseTime;
    private Boolean released;

    @JoinColumn(nullable = false, unique = true)
    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Column
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @ManyToOne
    @JoinColumn(name = "holder_id")
    public User getHolder() {
        return holder;
    }

    public void setHolder(User holder) {
        this.holder = holder;
    }

    @Column()
    public Timestamp getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(Timestamp holdTime) {
        this.holdTime = holdTime;
    }

    @Column()
    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp releaseTime) {
        this.releaseTime = releaseTime;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 1")
    public Boolean isReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }
}
