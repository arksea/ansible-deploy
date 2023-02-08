export class User {
    id: number;
    name: string;
    email: string;
    registerDate: string;
    lastLogin: string;
    locked: boolean;
    roles: Array<Role>;
}

export class Role {
    id: number;
    role: string;
    description: string;
    available: boolean;
    permissions: Array<Permission>;
}

export class Permission {
    id: number;
    permission: string;  //权限
    description: string; //描述
    pid: number;         //父权限ID
    available: boolean;  //是否生效
}