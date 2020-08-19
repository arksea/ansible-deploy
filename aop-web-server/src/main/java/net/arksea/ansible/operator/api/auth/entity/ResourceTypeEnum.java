package net.arksea.ansible.operator.api.auth.entity;

public enum ResourceTypeEnum {
    USER(2000, "用户"),
    ROLE(2100, "角色"),
    PERMISSION(2200, "权限");

    public int key;

    public String name;

    ResourceTypeEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static ResourceTypeEnum valueOf(int id) {
        ResourceTypeEnum[] enums = ResourceTypeEnum.values();
        for (ResourceTypeEnum e : enums) {
            if (e.key == id) return e;
        }
        return null;
    }
}
