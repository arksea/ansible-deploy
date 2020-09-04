import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { App } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';
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

@Injectable()
export class AppsService {
    private EMETY_SET: Set<string> = new Set();
    private _currentApp: App = new App();
    private appsModelInfo: AppsModelInfo = new AppsModelInfo();
    public appsModel: CrudModel<number, App> = new CrudModel<number, App>(this.appsModelInfo);
    public appList: Subject<App[]> = this.appsModel.modelList;
    public sortTypes: Array<SortType> = [{type:"name", order:"asc", desc:"应用名-升序"},
                                         {type:"name", order:"desc",desc:"应用名-降序"}]
    public selectedSortType: BehaviorSubject<SortType> = new BehaviorSubject(this.sortTypes[0])
    // private opAddVersion: Subject<AddModel<K,T>> = new Subject();

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

    public saveApp(app: App): Observable<ServiceResponse<number>> {
        let url = environment.apiUrl + '/api/apps';
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpPost('保存应用', url, app);
        return ret.pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        if (app.id == null) {
                            app.id = resp.result;
                            this.appsModel.opSetModel.next(app);
                        } else {
                            this.appsModel.opSetModel.next(app);
                        }
                    }
                    return resp;
                }
            )
        )
    }

    public deleteApp(appId: number): Observable<boolean> {
        let url = environment.apiUrl + '/api/apps/' + appId;
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpDelete('删除应用', url);
        return ret.pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        this.appsModel.opDelModel.next(appId);
                        return true;
                    } else {
                        return false;
                    }
                }
            )
        )
    }

    public updateCurrnetAppById(id: number): Observable<App> {
        let url = environment.apiUrl + '/api/apps/'+id;
        let ret: Observable<ServiceResponse<App>> = this.httpUtils.httpGet('查询应用', url);
        return ret.pipe(map(
            resp => {
                if (resp.code == 0) {
                    this._currentApp = resp.result;
                }
                return this._currentApp;
            }
        ));
    }

    public queryUserApps() {
        let url = environment.apiUrl + '/api/user/apps';
        let ret: Observable<ServiceResponse<Array<App>>> = this.httpUtils.httpGet('查询用户应用', url);
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

    public createVersion(appId: number, version: Version): Observable<string> {
        const url = environment.apiUrl + '/api/apps/' + appId + '/vers';
        return this.httpUtils.httpPost('新增版本', url, version).pipe(
            map(response => {
                    if (response.code === 0) {
                        // version.id = response.result;
                        // this.app.versions.
                        // this.appsModel.opSetModel.next();
                        // return null;
                    } else {
                        return response.error;
                    }
                }
            )
        );
    }



    get app(): App {
        return this._currentApp;
    }

    set app(app: App) {
        this._currentApp = app;
    }
}
