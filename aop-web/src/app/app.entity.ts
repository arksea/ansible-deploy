import { User } from "./users/users.entity";

export class GroupVar {
  id: number;
  name: string; // 变量名
  value: string;// 变量值
  isPort: boolean;// 是否端口值，用于主机范围的唯一性判断
}

export class Port {
  id: number;
  typeId: number;
  value: number;
  enabled: number;
  appId: number;
}

export class Version {
  id: number = undefined;
  appId: number = undefined;
  name: string = '';
  repository: string = '';
  javaOpt: string = '';
  revision: string = 'HEAD';
  targetHosts: Array<Host> = [];
}

export class Host {
  id: number;
  publicIp: string;  //公网IP
  privateIp: string; //内网IP
  description: string; //主机用途描述
  appGroupId: number; //所属分组
  enabled: boolean;  //是否可用状态
  createTime: string;//创建时间
}

export class App {
  id: number;
  apptag: string = '';     //应用标签，通常用来部署时建立应用目录名
  apptype: string = '';    //应用的类型
  deployPath: string = ''; //应用部署目标路径
  description: string = '';  //应用描述
  appGroupId: number; //应用所属分组
  vars: Array<GroupVar> = [];// 变量
  enableJmx: boolean = true;
  versions: Array<Version> = [];
  createTime: string; //创建时间
  deleted: boolean;
}

export class AppGroup {
  id: number;
  name: string;         // 分组名称
  description: string;  // 分组描述
  avatar: string;       // 分组头像
  apps: Array<App>;    // 分组管理的应用
  hosts: Array<Host>;  // 分组管理的主机
  users: Array<User>;  // 加入分组的用户

  public constructor() {
      this.apps = [];
      this.hosts = [];
      this.users = [];
  }
}

export class PortSection {
  id: number = undefined;
  type: PortType;
  minValue: number;
  maxValue: number;

}

export class PortType {
  id: number;
  name: string;
  description: string = '';
}
