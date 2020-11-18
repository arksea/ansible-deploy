import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'
import { map } from 'rxjs/operators'
import { App, AppGroup, AppType, OperationJob, Version, AppVarDefine } from '../app.entity'
import { AppOperation, Host, Port, OperationJobPage, UserAppsPage } from '../app.entity'

class StartOpeartionJob {
    appId: number
    versionId: number
    operationId: number
    hosts: Array<number> = []
}

export class PollLogsResult {
    log: string
    index: number
    size: number
}

@Injectable()
export class AppsService {
    private varDefineMap: Map<string,AppVarDefine> = new Map()

    public constructor(private httpUtils: HttpUtils) {
    }

    public saveApp(app: App): Observable<ServiceResponse<App>> {
        let url = environment.apiUrl + '/api/apps'
        return this.httpUtils.httpPost('保存应用', url, app)
    }

    public deleteApp(appId: number): Observable<boolean> {
        let url = environment.apiUrl + '/api/apps/' + appId
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpDelete('修改应用删除状态', url)
        return ret.pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        return true
                    } else {
                        return false
                    }
                }
            )
        )
    }

    public queryVarDefine() {
        let url = environment.apiUrl + '/api/varDefines'
        this.httpUtils.httpGet('查询变量定义',url).subscribe(ret => {
            if (ret.code == 0) {
                for (let def of ret.result) {
                    let key = '' + def.appTypeId + ':' + def.name
                    this.varDefineMap[key] = def
                }
            }
        })
    }

    public getUserApps(page: number, pageSize: number, nameSearch: string): Observable<ServiceResponse<UserAppsPage>> {
        let url = environment.apiUrl + '/api/user/apps?page='+page+'&pageSize='+pageSize
        if (nameSearch != '') {
            url = url + '&nameSearch='+nameSearch
        }
        return this.httpUtils.httpGet('查询用户应用', url)
    }

    public createAppTemplate(appType: string): Observable<ServiceResponse<App>> {
        let url = environment.apiUrl + '/api/apps/template/'+appType
        return this.httpUtils.httpGet('获取应用创建模版', url)
    }

    public getAppVarDefine(appTypeId: number, name: string): AppVarDefine {
        let key = '' + appTypeId + ':' + name
        return this.varDefineMap[key]
    }

    public createDefAppTemplate(): App {
        let app = new App()
        app.apptag = ''
        app.appType = new AppType()
        app.appType.id = 3
        app.appType.name = 'Command'
        app.description = ''
        app.vars = []
        let ver = new Version()
        ver.name = 'Online'
        ver.repository = 'trunk'
        ver.revision = 'HEAD'
        ver.execOpt = ''
        app.versions = [ver]
        return app
    }

    public deleteVersionById(versionId: number): Observable<boolean> {
        const url = environment.apiUrl + '/api/versions/' + versionId
        return this.httpUtils.httpDelete('删除版本', url).pipe(
            map(response => {
                    if (response.code === 0) {
                        return true
                    } else {
                        return false
                    }
                }
            )
        )
    }

    public saveVersion(version: Version): Observable<number> {
        const url = environment.apiUrl + '/api/versions'
        return this.httpUtils.httpPost('保存版本', url, version).pipe(
            map(response => {
                    if (response.code === 0) {
                        return response.result
                    } else {
                        return undefined
                    }
                }
            )
        )
    }

    public addHostsToVersion(versionId: number, hosts: number[]): Observable<string> {
        const url = environment.apiUrl + '/api/versions/' + versionId + '/hosts'
        return this.httpUtils.httpPost('版本新增部署主机', url, hosts).pipe(
            map(response => {
                    if (response.code === 0) {
                        return null
                    } else {
                        return response.error
                    }
                }
            )
        )
    }

    public removeHostFromVersion(versionId: number, hostId: number): Observable<boolean> {
        const url = environment.apiUrl + '/api/versions/' + versionId + '/hosts/' + hostId
        return this.httpUtils.httpDelete('版本新增部署主机', url).pipe(map(r =>  r.code === 0))
    }

    public getAppById(appId: number): Observable<ServiceResponse<App>> {
        const url = environment.apiUrl + '/api/apps/' + appId
        return this.httpUtils.httpGet('查询应用', url)
    }

    public getUserGroups(): Observable<ServiceResponse<Array<AppGroup>>> {
        let url = environment.apiUrl + '/api/user/groups'
        return this.httpUtils.httpGet('查询用户的分组', url)
    }

    public startJob(app: App, ver: Version, operation: AppOperation,hosts: Array<Host>): Observable<ServiceResponse<OperationJob>> {
        const url = environment.apiUrl + '/api/jobs'
        const body = new StartOpeartionJob()
        body.appId = app.id
        if (ver) {
            body.versionId = ver.id
        }
        body.operationId = operation.id
        body.hosts = []
        for (let h of hosts) {
            body.hosts.push(h.id)
        }
        return this.httpUtils.httpPost('开始操作任务', url, body)
    }

    public pollJobLogs(jobId: number, index: number): Observable<ServiceResponse<PollLogsResult>> {
        const url = environment.apiUrl + '/api/jobs/' + jobId + '/logs/' + index 
        return this.httpUtils.httpGet('读取操作日志', url)
    }

    public getOperationsByAppTypeId(appTypeId: number): Observable<ServiceResponse<AppOperation[]>> {
        const url = environment.apiUrl + '/api/appTypes/'+appTypeId+'/operations'
        return this.httpUtils.httpGet('查询应用操作', url)
    }

    public searchPortsByPrefix(prefix: string, limit: number): Observable<ServiceResponse<Array<Port>>> {
        const url = environment.apiUrl + '/api/ports/prefix/'+prefix+"?limit="+limit
        return this.httpUtils.httpGet('搜索端口', url)
    }

    public getPortByValue(value: number): Observable<ServiceResponse<Array<Port>>> {
        const url = environment.apiUrl + '/api/ports/'+value
        return this.httpUtils.httpGet('查询端口', url)
    }

    public getAppTypes(): Observable<ServiceResponse<Array<AppType>>> {
        const url = environment.apiUrl + '/api/appTypes'
        return this.httpUtils.httpGet('查询应用类型', url)
    }

    public getAppOperationJobHistory(appId: number,
                page: number, pageSize: number,
                startTime: string, endTime: string,
                operator: string): Observable<ServiceResponse<OperationJobPage>> {
        let url = environment.apiUrl + '/api/apps/' + appId + '/operations?page='+page+'&pageSize='+pageSize
        if (startTime != '') {
            url = url + '&startTime='+startTime
        }
        if (endTime != '') {
            url = url + '&endTime='+endTime
        }
        if (operator != '') {
            url = url + '&operator=' + encodeURI(operator)
        }
        return this.httpUtils.httpGet('查询应用操作记录', url)
    }
}
