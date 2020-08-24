import { User } from "../account/account.entity";

export class GroupVar {
  id: number;
  name: string; // 变量名
  value: string;// 变量值
  describes: string; // 参数描述
}

export class Port {
  id: number;
  type: number;
  name: string;
  value: number;
  enable: number;
}

export class Version {
  id: number;
  name: string
  repository: string
  javaOpt: string;
  revision: string;
  targetHosts: Array<Host>;
}

export class Host {
  id: number;
  publicIp: string;  //公网IP
  privateIp: string; //内网IP
  describes: string; //主机用途描述
  createTime: string;//创建时间
}

export class App {
  id: number;
  apptag: string;     //应用标签，通常用来部署时建立应用目录名
  apptype: string;    //应用的类型
  deployPath: string; //应用部署目标路径
  describes: string;  //应用描述
  vars: Array<GroupVar>;// 变量
  ports: Array<Port>;
  enableJmx: boolean;
  versions: Array<Version>;
  createTime: string; //创建时间
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
