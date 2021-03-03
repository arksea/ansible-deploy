package net.arksea.ansible.deploy.api.manage.entity;
import javax.persistence.*;

/**
 * Create by xiaohaixing on 2020/9/24
 */
@Entity
@Table(name = "dp2_app_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appTypeId", "name" })
)
public class AppVarDefine extends VarDefine {
}
