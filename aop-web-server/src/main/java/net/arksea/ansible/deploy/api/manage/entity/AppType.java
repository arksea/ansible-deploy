package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_type")
public class AppType extends IdEntity {
    private String name;
    private String description;
    private Set<AppVarDefine> appVarDefines;
    private Set<VersionVarDefine> versionVarDefines;

    @Column(nullable = false, length = 64, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 256, unique = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "appTypeId", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<AppVarDefine> getAppVarDefines() {
        return appVarDefines;
    }

    public void setAppVarDefines(Set<AppVarDefine> appVarDefines) {
        this.appVarDefines = appVarDefines;
    }

    @OneToMany(mappedBy = "appTypeId", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<VersionVarDefine> getVersionVarDefines() {
        return versionVarDefines;
    }

    public void setVersionVarDefines(Set<VersionVarDefine> versionVarDefines) {
        this.versionVarDefines = versionVarDefines;
    }
}
