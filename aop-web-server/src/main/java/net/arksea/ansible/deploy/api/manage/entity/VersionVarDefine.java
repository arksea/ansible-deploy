package net.arksea.ansible.deploy.api.manage.entity;
import javax.persistence.*;

/**
 * Create by xiaohaixing on 2021/03/02
 */
@Entity
@Table(name = "dp2_version_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appTypeId", "name" })
)
public class VersionVarDefine extends VarDefine {
}
