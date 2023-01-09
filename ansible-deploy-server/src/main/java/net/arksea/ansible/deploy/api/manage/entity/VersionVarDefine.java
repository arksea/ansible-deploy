package net.arksea.ansible.deploy.api.manage.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Create by xiaohaixing on 2021/03/02
 */
@Entity
@Table(name = "dp2_version_var_define",
        uniqueConstraints = @UniqueConstraint(columnNames = { "appTypeId", "name" })
)
public class VersionVarDefine extends VarDefine {
}
