package net.arksea.ansible.deploy.api.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "dp2_tomcat_config_file")
public class TomcatConfigeFile extends IdEntity {
    private String filename;

    private byte[] filecontent;

    private App app;
    
    @Column(name = "file_name", length = 100, nullable = false)
    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_content", columnDefinition = "BLOB", nullable=true)
    public byte[] getFilecontent() {
        return filecontent;
    }

    public void setFilecontent(final byte[] filecontent) {
        this.filecontent = filecontent;
    }

    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "app_id")
    @JsonIgnore
    public App getApp() {
        return app;
    }

    public void setApp(final App app) {
        this.app = app;
    }
}
