package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.*;
//import java.util.Set;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_type")
public class AppType extends IdEntity {
    private String name;
//    private Set<AppVarDefine> definitions;

    @Column(nullable = false, length = 64, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @OneToMany(mappedBy = "appTypeId", fetch = FetchType.EAGER)
//    @OrderBy("id")
//    public Set<AppVarDefine> getDefinitions() {
//        return definitions;
//    }
//
//    public void setDefinitions(Set<AppVarDefine> definitions) {
//        this.definitions = definitions;
//    }
}
