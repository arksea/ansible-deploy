import { User } from "./users/users.entity"

export class PortType {
    id: number|null = null
    name: string = ''
    description: string = ''
    allCount: number = 0
    restCount: number = 0
}

export class AppVarDefine {
    id: number|null = null
    appTypeId: number|null = null
    name: string = ''// 变量名
    formLabel: string = ''  //表单输入框标签
    inputAddon: string = '' //表单输入框提示前缀
    defaultValue: string = ''
    portType: PortType|null = null
}

export class VersionVarDefine {
    id: number|null = null
    appTypeId: number|null = null
    name: string = ''// 变量名
    formLabel: string = ''  //表单输入框标签
    inputAddon: string = '' //表单输入框提示前缀
    defaultValue: string = ''
    portType: PortType|null = null
}

export class OperationVarDefine {
    id: number|null = null
    operationId: number|null = null
    name: string = ''// 变量名
    formLabel: string = ''  //表单输入框标签
    inputAddon: string = '' //表单输入框提示前缀
    defaultValue: string = ''
}

export class AppType {
    id: number|null = null
    name: string = ''
    description: string = ''
    appVarDefines: Array<AppVarDefine> = []
    versionVarDefines: Array<VersionVarDefine> = []
}

export class AppVariable {
    id: number|null = null
    name: string = ''// 变量名
    value: string = ''// 变量值
    isPort: boolean = false// 是否端口值，用于主机范围的唯一性判断
    deleted: boolean = false
}

export class VersionVariable {
    id: number|null = null
    name: string = ''// 变量名
    value: string = ''// 变量值
    isPort: boolean = false// 是否端口值，用于主机范围的唯一性判断
    deleted: boolean = false
}

export class OperationTrigger {
    id: number|null = null
    versionId: number
    operationId: number
    projectTag: string = ''
    token: string = ''
    description: string = ''
    createUser: string = ''
    createTime: number = 0
    expiredTime: number = 0
    notifyEmails: string = '';
    notifyRegex: string = '';
    notifyMatchOrNot: boolean = true;
}

export class AppOperation {
    id: number|null = null
    appType: AppType = new AppType()
    name: string = ''
    description: string = ''
    command: string = ''
    codes: Array<AppOperationCode> = []
    varDefines: Array<OperationVarDefine> = []
    released: boolean = false
    type: string = 'COMMON'
}

export class AppOperationCode {
    id: number|null = null
    operationId: number|null = null
    fileName: string = ''
    code: string = ''
}

export class AppCustomOperationCode {
    id: number|null = null
    appId: number|null = null
    operationId: number|null = null
    operationName: string = ''
    fileName: string = ''
    code: string = ''
}

export class Port {
    id: number|null = null
    typeId: number|null = null
    value: number|null = null
    enabled: number = 1
    appId: number|null = null
}

export class Version {
    id: number|null = null
    appId: number|null = null
    name: string = ''
    repository: string = 'trunk' //部署包仓库分支路径
    execOpt: string = ''         //运行参数
    buildNo: number = 0          //最近构建
    buildNoUpdate: number = 0    //更新构建号的时间
    deployNo: number = 0         //当前部署
    deployNoUpdate: number = 0   //部署时间
    revision: string = 'HEAD'
    vars: Array<VersionVariable> = []// 变量
    targetHosts: Array<Host> = []    // 部署目标主机
    triggers: Array<OperationTrigger> = [] //操作触发器
}

export class HostStatus {
    value: string = ''
    color: string = ''
}
export class Host {
    id: number|null = null
    publicIp: string|null = null  //公网IP
    privateIp: string|null = null //内网IP
    description: string = ''//主机用途描述
    appGroup: AppGroup|null = null //所属分组
    enabled: boolean = false  //是否可用状态
    createTime: string|null = null//创建时间
    status: Map<string, HostStatus> = new Map()
}

export class App {
    id: number|null = null
    apptag: string = ''     //应用标签，通常用来部署时建立应用目录名
    appType: AppType|null = null    //应用的类型
    description: string = ''  //应用描述
    appGroup: AppGroup|null = null //应用所属分组
    vars: Array<AppVariable> = []// 变量
    versions: Array<Version> = []
    createTime: string|null = null //创建时间
}

export class AppInfo {
    id: number|null = null
    apptag: string = '' //应用标签，通常用来部署时建立应用目录名
    appTypeId: number|null = null
}

export class AppGroup {
    id: number|null = null
    name: string = ''        // 分组名称
    description: string = ''  // 分组描述
    avatar: string = ''      // 分组头像
    appCount: number  = -1  // 分组管理的应用
    hostCount: number  = -1// 分组管理的主机
    userCount: number = -1 // 加入分组的用户
}

export class PortSection {
    id: number|null = null
    type: PortType|null = null
    minValue: number|null = null
    maxValue: number|null = null

}

export class OperationJob {
    id: number|null = null
    appId: number|null = null
    operatorId: number|null = null
    operationId: number|null = null
    triggerId: number|number = null
    execHost: string = ''
    startTime: string|null = null
    endTime: string|null = null
    log: string = ''
}

export class OperationJobInfo {
    jobId: number|null = null
    operation: string = ''
    operator: string = ''
    triggerId: number|null = null
    version: string = ''
    startTime: string = ''
    endTime: string = ''
}

export class Page<T> {
    total: number = 0
    totalPages: number = 0
    items: Array<T> = []
}

export class OperationVariable {
    name: string
    value: string
}