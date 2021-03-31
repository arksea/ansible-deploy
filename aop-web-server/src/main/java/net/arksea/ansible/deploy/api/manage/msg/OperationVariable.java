package net.arksea.ansible.deploy.api.manage.msg;

/**
 *
 * @author xiaohaixing
 */

public class OperationVariable {
    private String name;// 变量名
    private String value;// 变量值

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(final String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
