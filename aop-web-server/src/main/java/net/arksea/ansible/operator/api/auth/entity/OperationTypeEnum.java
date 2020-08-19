package net.arksea.ansible.operator.api.auth.entity;

public enum OperationTypeEnum {
    ADD(1, "添加"),
    UPDATE(2, "修改"),
    DELETE(3, "删除");

    public int key;
    public String name;

    OperationTypeEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static OperationTypeEnum valueOf(int id) {
        OperationTypeEnum[] enums = OperationTypeEnum.values();
        for (OperationTypeEnum e : enums) {
            if (e.key == id) return e;
        }
        return null;
    }
}
