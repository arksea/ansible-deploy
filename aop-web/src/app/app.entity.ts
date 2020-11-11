import { User } from "./users/users.entity"

export class PortType {
    id: number = undefined
    name: string = ''
    description: string = ''
    allCount: number = 0
    restCount: number = 0
}

export class AppVarDefine {
    id: number
    appTypeId: number
    name: string = ''// 变量名
    formLabel: string = ''  //表单输入框标签
    inputAddon: string = '' //表单输入框提示前缀
    defaultValue: string = ''
    portType: PortType
}

export class AppType {
    id: number
    name: string = ''
    description: string = ''
    appVarDefines: Array<AppVarDefine> = []
}

export class AppVariable {
    id: number
    name: string // 变量名
    value: string// 变量值
    isPort: boolean// 是否端口值，用于主机范围的唯一性判断
}

export class AppOperation {
    id: number
    appType: AppType = new AppType()
    name: string = ''
    description: string = ''
    command: string = ''
    codes: Array<AppOperationCode> = []
    released: boolean = false
    type: string = 'COMMON'
}

export class AppOperationCode {
    id: number
    operationId: number
    fileName: string
    description: string = ''
    code: string = ''
}

export class Port {
    id: number
    typeId: number
    value: number
    enabled: number
    appId: number
}

export class Version {
    id: number = undefined
    appId: number = undefined
    name: string = ''
    repository: string = ''
    execOpt: string = ''
    revision: string = 'HEAD'
    targetHosts: Array<Host> = []
}

export class HostStatus {
    value: string
    color: string
}
export class Host {
    id: number
    publicIp: string  //公网IP
    privateIp: string //内网IP
    description: string //主机用途描述
    appGroupId: number //所属分组
    enabled: boolean  //是否可用状态
    createTime: string//创建时间
    status: Map<string, HostStatus> = new Map()
}

export class App {
    id: number;
    apptag: string = ''     //应用标签，通常用来部署时建立应用目录名
    appType: AppType    //应用的类型
    description: string = ''  //应用描述
    appGroupId: number //应用所属分组
    vars: Array<AppVariable> = []// 变量
    enableJmx: boolean = true
    versions: Array<Version> = []
    createTime: string //创建时间
}

export class AppGroup {
    id: number = undefined
    name: string         // 分组名称
    description: string  // 分组描述
    avatar: string       // 分组头像
    apps: Array<App>    // 分组管理的应用
    hosts: Array<Host>  // 分组管理的主机
    users: Array<User>  // 加入分组的用户

    public constructor() {
        this.apps = []
        this.hosts = []
        this.users = []
    }
}

export class PortSection {
    id: number = undefined
    type: PortType
    minValue: number
    maxValue: number

}

export class OperationJob {
    id: number
    appId: number
    operatorId: number
    operationId: number
    execHost: string
    startTime: string
    endTime: string
    log: string
}