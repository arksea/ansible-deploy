import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { App, AppGroup, AppType, OperationJob } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';
import { map, first } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';
import { Version, AppVarDefine, AppOperation } from '../app.entity';

class AppsModelInfo implements IModelInfo<number, App> {
    private sortType: string;
    private sortOrder: string; //正序asc或逆序desc
    public getModelMapKey(t: App): number {
        return t.id;
    }
    public getSortKey(t: App): any {
        if (this.sortType == 'name') {
            return t.apptag;
        } else {
            return t.apptag;
        }
    }
    public getSortOrder(): string {
        return this.sortOrder;
    }
    public needSort(): boolean {
        return true;
    }
    public setSortType(type: string, order: string) {
        this.sortType = type;
        this.sortOrder = order;
    }
}

class SortType {
    type: string;
    order: string;
    desc: string;
}

class StartOpeartionJob {
    appId: number;
    operationId: number;
    hosts: Array<number> = [];
}

export class PollLogsResult {
    log: string;
    index: number;
    size: number;
}

@Injectable()
export class AppsService {
    private EMETY_SET: Set<string> = new Set();
    public userGroups: Subject<AppGroup[]> = new BehaviorSubject([]);
    private appsModelInfo: AppsModelInfo = new AppsModelInfo();
    public appsModel: CrudModel<number, App> = new CrudModel<number, App>(this.appsModelInfo);
    public appList: Subject<App[]> = this.appsModel.modelList;
    public sortTypes: Array<SortType> = [{type:"name", order:"asc", desc:"应用名-升序"},
                                         {type:"name", order:"desc",desc:"应用名-降序"}]
    public selectedSortType: BehaviorSubject<SortType> = new BehaviorSubject(this.sortTypes[0])
    private varDefineMap: Map<string,AppVarDefine> = new Map();

    public constructor(private httpUtils: HttpUtils) {
    }

    public getSortDesc(index: number): string {
        let cfg = this.sortTypes[index];
        return cfg.desc;
    }

    public setSortType(cfg) {
        this.selectedSortType.next(cfg);
        this.appsModelInfo.setSortType(cfg.type, cfg.order);
        this.appsModel.opUpdateSort.next(true);        
    }

    public saveApp(app: App): Observable<App> {
        let url = environment.apiUrl + '/api/apps';
        return this.httpUtils.httpPost('保存应用', url, app).pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        return resp.result;
                    }
                    return undefined;
                }
            )
        )
    }

    public updateDeleted(appId: number, deleted: boolean): Observable<boolean> {
        let url = environment.apiUrl + '/api/apps/' + appId + '/deleted';
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpPut('修改应用删除状态', url, deleted);
        return ret.pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            )
        )
    }

    public queryUserApps() {
        let url = environment.apiUrl + '/api/user/apps';
        this.httpUtils.httpGet('查询用户应用', url).subscribe(ret => {
            if (ret.code == 0) {
                this.appsModel.opResetModels.next(ret.result);
            }
        });
        url = environment.apiUrl + '/api/user/groups';
        this.httpUtils.httpGet('查询用户的分组', url).subscribe(ret => {
            if (ret.code == 0) {
                this.userGroups.next(ret.result);
            }
        });
        url = environment.apiUrl + '/api/varDefines';
        this.httpUtils.httpGet('查询变量定义',url).subscribe(ret => {
            if (ret.code == 0) {
                for (let def of ret.result) {
                    let key = '' + def.appType.id + ':' + def.name;
                    this.varDefineMap[key] = def;
                }
            }
        })
    }

    public createAppTemplate(appType: string): Observable<ServiceResponse<App>> {
        let url = environment.apiUrl + '/api/apps/template/'+appType;
        return this.httpUtils.httpGet('获取应用创建模版', url)
    }

    public getAppVarDefine(appTypeId: number, name: string): AppVarDefine {
        let key = '' + appTypeId + ':' + name;
        return this.varDefineMap[key];
    }

    public createDefAppTemplate(): App {
        let app = new App();
        app.apptag = '';
        app.appType = new AppType();
        app.appType.id = 3;
        app.appType.name = 'Command';
        app.deployPath = '';
        app.description = '';
        app.enableJmx = true;
        app.vars = [];
        let ver = new Version();
        ver.name = 'Online';
        ver.repository = 'trunk';
        ver.revision = 'HEAD';
        ver.execOpt = ''
        app.versions = [ver];
        return app;
    }

    public deleteVersionById(versionId: number): Observable<boolean> {
        const url = environment.apiUrl + '/api/versions/' + versionId;
        return this.httpUtils.httpDelete('删除版本', url).pipe(
            map(response => {
                    if (response.code === 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            )
        );
    }

    public saveVersion(version: Version): Observable<number> {
        const url = environment.apiUrl + '/api/versions';
        return this.httpUtils.httpPost('保存版本', url, version).pipe(
            map(response => {
                    if (response.code === 0) {
                        return response.result;
                    } else {
                        return undefined;
                    }
                }
            )
        );
    }

    public addHostsToVersion(versionId: number, hosts: number[]): Observable<string> {
        const url = environment.apiUrl + '/api/versions/' + versionId + '/hosts';
        return this.httpUtils.httpPost('版本新增部署主机', url, hosts).pipe(
            map(response => {
                    if (response.code === 0) {
                        return null;
                    } else {
                        return response.error;
                    }
                }
            )
        );
    }

    public removeHostFromVersion(versionId: number, hostId: number): Observable<boolean> {
        const url = environment.apiUrl + '/api/versions/' + versionId + '/hosts/' + hostId;
        return this.httpUtils.httpDelete('版本新增部署主机', url).pipe(map(r =>  r.code === 0));
    }

    public getAppByApptag(apptag: string): Observable<App> {
        return this.appList.pipe(first(),map(list => {
            for (let a of list) {
                if (a.apptag == apptag) {
                    return a;
                }
            }
            return null;
        }))
    }

    public getAppById(appId: number): Observable<App> {
        return this.appsModel.modelData.pipe(first(),map(data => {
            return data.map.get(appId)
        }))
    }

    public getSelectedApp(): Observable<App> {
        return this.appsModel.modelData.pipe(first(),map(data => {
            return data.map.get(data.selected);
        }))
    }

    public startJob(): Observable<ServiceResponse<OperationJob>> {
        const url = environment.apiUrl + '/api/jobs';
        const body = new StartOpeartionJob();
        body.appId = 9;
        body.operationId = 5;
        body.hosts.push(1);
        body.hosts.push(2);
        return this.httpUtils.httpPost('开始操作任务', url, body);
    }

    public pollJobLogs(jobId: number, index: number): Observable<ServiceResponse<PollLogsResult>> {
        const url = environment.apiUrl + '/api/jobs/' + jobId + '/logs/' + index ;
        return this.httpUtils.httpGet('读取操作日志', url);
    }

    public getOperationsByAppTypeId(appTypeId: number): Observable<ServiceResponse<AppOperation[]>> {
        const url = environment.apiUrl + '/api/operations/?appTypeId=' + appTypeId;
        return this.httpUtils.httpGet('查询操作', url);
    }
}
