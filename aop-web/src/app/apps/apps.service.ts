import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { App, AppGroup } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';
import { map, first } from 'rxjs/operators';
import { CrudModel, IModelInfo, IModelMapOperation, ModelMap, ModelData } from '../utils/crud-model';
import { Version } from '../app.entity';

// type ChildPermMap = Map<string, Set<string>>;

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

class AddVersion {
    constructor(public appId: number, public version: Version) {}
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

    public saveApp(app: App): Observable<number> {
        let url = environment.apiUrl + '/api/apps';
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpPost('保存应用', url, app);
        return ret.pipe(
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
        this.httpUtils.httpGet('查询用户应用', url).subscribe(data => {
            if (data.code == 0) {
                this.appsModel.opResetModels.next(data.result);
            }
        });
        url = environment.apiUrl + '/api/user/groups';
        this.httpUtils.httpGet('查询用户的分组', url).subscribe(data => {
            if (data.code == 0) {
                this.userGroups.next(data.result);
            }
        });
    }

    public queryNotInGroupApps() {
        let url = environment.apiUrl + '/api/apps/notInGroup';
        let ret: Observable<ServiceResponse<Array<App>>> = this.httpUtils.httpGet('查询所有应用', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.appsModel.opResetModels.next(data.result);
            }
        });
    }

    public newTomcatApp(): App {
        let app = new App();
        app.apptag = '';
        app.apptype = 'tomcat';
        app.deployPath = '';
        app.description = '';
        app.enableJmx = true;
        app.vars = [{id:null,description: '域名', value: 'localhost', name:'domain', inputAddon: '', isPort: false, inputType: 'text'},
                    {id:null,description: 'ContextPath, URL路径', value: '', name:'context_path', inputAddon: 'http://domain/', isPort: false, inputType: 'text'}]
        app.versions = [];
        return app;
    }

    public deployPathAddon(apptype: string) {
        switch (apptype) {
            case 'tomcat':
                return '$HOME/tomcat/webapps/';
            default:
                return '$HOME/';
        }
    }

    public createVersion(appId: number, version: Version): Observable<number> {
        const url = environment.apiUrl + '/api/apps/' + appId + '/vers';
        return this.httpUtils.httpPost('新增版本', url, version).pipe(
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
}
