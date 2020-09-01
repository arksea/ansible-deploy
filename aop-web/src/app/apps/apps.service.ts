import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { App } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';

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
    public editingApp: BehaviorSubject<App> = new BehaviorSubject<App>(this.newTomcatApp());
    private appsModelInfo: AppsModelInfo = new AppsModelInfo();
    public appsModel: CrudModel<number, App> = new CrudModel<number, App>(this.appsModelInfo);
    public appList: Subject<App[]> = this.appsModel.modelList;
    public sortTypes: Array<SortType> = [{type:"name", order:"asc", desc:"应用名-正序"},
                                         {type:"name", order:"desc",desc:"应用名-逆序"}]
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

    // public createGroup(name: string, description: string): Observable<string> {
    //     let n = encodeURI(name);
    //     let d = encodeURI(description);
    //     const url = environment.apiUrl + '/api/groups?name=' + n + '&desc=' + d;
    //     return this.httpUtils.httpPost('新建组', url, '').pipe(
    //         map(response => {
    //                 if (response.code === 0) {
    //                     let group: User = new User()
    //                     group.id = response.result;
    //                     group.name = name;
    //                     group.description = description;
    //                     this.model.opAddModel.next({key: response.result, value: group});
    //                     return null;
    //                 } else {
    //                     return response.error;
    //                 }
    //             }
    //         )
    //     );
    // }

    public saveApp(app: App): Observable<ServiceResponse<number>> {
        let url = environment.apiUrl + '/api/apps';
        let ret: Observable<ServiceResponse<number>> = this.httpUtils.httpPost('保存应用', url, app);
        return ret.pipe(
            map (
                resp => {
                    if (resp.code == 0) {
                        if (app.id == null) {
                            app.id = resp.result;
                            this.appsModel.opAddModel.next({key: app.id, value: app});
                        } else {
                            this.appsModel.opUpdateModel.next(app);
                        }
                    }
                    return resp;
                }
            )
        )
    }

    public getAppById(id: number): Observable<ServiceResponse<App>> {
        let url = environment.apiUrl + '/api/apps/'+id;
        let ret: Observable<ServiceResponse<App>> = this.httpUtils.httpGet('查询应用', url);
        return ret;
    }

    public queryUserApps() {

        let url = environment.apiUrl + '/api/user/apps';
        let ret: Observable<ServiceResponse<Array<App>>> = this.httpUtils.httpGet('查询用户应用', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.appsModel.opResetModels.next(data.result);
            }
        });

        // let app1 = new App();
        // app1.id = 1;
        // app1.apptag = 'app1';
        // app1.description = '黄历天气API服务';
        // app1.apptype = 'Tomcat';
        // app1.vars = [{id:1,description: 'HTTP端口',   value: '8061', name:'http_port', inputAddon: '', isPort: true, inputType: ''},
        //         {id:2,description: 'HTTPS端口',  value:'8461', name:'https_port', inputAddon: '', isPort: true, inputType: ''},
        //         {id:3,description: '服务管理端口',value: '8260', name:'server_port', inputAddon: '', isPort: true, inputType: ''},
        //         {id:4,description: 'AJP协议端口', value: '8660', name:'ajp_port', inputAddon: '', isPort: true, inputType: ''},
        //         {id:5,description: 'JMX管理端口', value: '8931', name:'jmx_port', inputAddon: '', isPort: true, inputType: ''},
        //         {id:6,description: '域名', value: 'localhost', name:'domain', inputAddon: '', isPort: false, inputType: ''},
        //         {id:7,description: 'ContextPath, URL路径', value: '', name:'context_path', inputAddon: '', isPort: false, inputType: ''}]
        // let app2 = new App();
        // app2.id = 2;
        // app2.apptag = 'app2';
        // app2.description = '精灵天气API服务';
        // app2.apptype = 'Command';
        // app2.vars = [{id:5,description: 'JMX管理端口', value: '8938', name:'jmx_port', inputAddon: '', isPort: true, inputType: ''}]
        // this.appsModel.opResetModels.next([app1,app2]);
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
}
