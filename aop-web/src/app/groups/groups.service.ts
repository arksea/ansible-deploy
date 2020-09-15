import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable, BehaviorSubject } from 'rxjs';
import { AppGroup } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { CrudModel, IModelInfo } from '../utils/crud-model';
import { debounceTime, distinctUntilChanged, map, flatMap, first } from 'rxjs/operators';
import { HostsService } from '../hosts/hosts.service';
import { Host } from '../app.entity';
import { IModelMapOperation, ModelData } from '../utils/crud-model';
import { User } from '../users/users.entity';
import { UsersService } from '../users/users.service';
import { App } from '../app.entity';
import { AppsService } from '../apps/apps.service';

class AppGroupModelInfo implements IModelInfo<number, AppGroup> {
    public getModelMapKey(t: AppGroup): number {
        return t.id;
    }
    public getSortKey(t: AppGroup): any {
        return t.id;
    }
    public needSort(): boolean {
        return true;
    }
    public getSortOrder(): string {
        return 'asc';
    }
}


@Injectable()
export class GroupsService {
    private EMETY_SET: Set<string> = new Set();
    public model: CrudModel<number, AppGroup> = new CrudModel<number, AppGroup>(new AppGroupModelInfo());
    public groupList: Subject<AppGroup[]> = this.model.modelList;
    private currentGroup: Subject<AppGroup> = this.model.modelSelected;
    private currentGroupId: number = undefined;
    private opAddHostToCurrentGroup: Subject<Host> = new Subject();
    private opRemoveHostFromCurrentGroup: Subject<Host> = new Subject();
    private opAddMemberToCurrentGroup: Subject<User> = new Subject();
    private opRemoveMemberFromCurrentGroup: Subject<User> = new Subject();
    private opAddAppToCurrentGroup: Subject<App> = new Subject();
    private opRemoveAppFromCurrentGroup: Subject<App> = new Subject();

    public constructor(private httpUtils: HttpUtils, 
            private router: Router, 
            private alert: MessageNotify,
            private hostsSvc: HostsService,
            private usersSvc: UsersService,
            private appsSvc: AppsService) {
        this.queryNotInGroupApps();
        this.opAddHostToCurrentGroup.pipe(
            map(function (host: Host): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    modelData.map.get(modelData.selected).hosts.push(host);
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);

        this.opRemoveHostFromCurrentGroup.pipe(
            map(function (host: Host): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    let g: AppGroup = modelData.map.get(modelData.selected);
                    let hosts: Host[] = [];
                    let old = g.hosts;
                    for (let h of old) {
                        if (h.id != host.id) {
                            hosts.push(h);
                        }
                    }
                    g.hosts = hosts;
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);

        this.opAddMemberToCurrentGroup.pipe(
            map(function (user: User): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    modelData.map.get(modelData.selected).users.push(user);
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);

        this.opRemoveMemberFromCurrentGroup.pipe(
            map(function (user: User): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    let g: AppGroup = modelData.map.get(modelData.selected);
                    let users: User[] = [];
                    let old = g.users;
                    for (let u of old) {
                        if (u.id != user.id) {
                            users.push(u);
                        }
                    }
                    g.users = users;
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);

        this.opAddAppToCurrentGroup.pipe(
            map(function (app: App): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    modelData.map.get(modelData.selected).apps.push(app);
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);

        this.opRemoveAppFromCurrentGroup.pipe(
            map(function (app: App): IModelMapOperation<number,AppGroup> {
                return (modelData: ModelData<number,AppGroup>) => {
                    let g: AppGroup = modelData.map.get(modelData.selected);
                    let apps: App[] = [];
                    let old = g.apps;
                    for (let a of old) {
                        if (a.id != app.id) {
                            apps.push(a);
                        }
                    }
                    g.apps = apps;
                    return modelData;
                }
            })
        ).subscribe(this.model.updates);
    }

    public createGroup(name: string, description: string): Observable<boolean> {
        let n = encodeURI(name);
        let d = encodeURI(description);
        const url = environment.apiUrl + '/api/groups?name=' + n + '&desc=' + d;
        return this.httpUtils.httpPost('新建组', url, '').pipe(
            map(response => {
                if (response.code === 0) {
                    let group: AppGroup = new AppGroup()
                    group.id = response.result;
                    group.name = name;
                    group.description = description;
                    this.model.opSetModel.next(group);
                    return true;
                } else {
                    return false;
                }
            })
        );
    }

    public queryGroups() {
        let ret = this.getGroups();
        ret.subscribe(data => {
            if (data.code == 0) {
                this.model.opResetModels.next(data.result);
            }
        });
    }

    public queryNotInGroupApps(): Observable<App[]> {
        let url = environment.apiUrl + '/api/apps/notInGroup';
        let ret: Observable<ServiceResponse<Array<App>>> = this.httpUtils.httpGet('查询未分组应用', url);
        return ret.pipe(map(data => {
            if (data.code == 0) {
                return data.result;
            } else {
                return [];
            }
        }));
    }

    public getGroups(): Observable<ServiceResponse<Array<AppGroup>>> {
        const url = environment.apiUrl + '/api/groups';
        return this.httpUtils.httpGet('查询组信息', url);
    }

    public getGroupById(id: number): Observable<ServiceResponse<AppGroup>> {
        const url = environment.apiUrl + '/api/groups/'+id;
        return this.httpUtils.httpGet('查询组信息', url);
    }

    public deleteGroup(group: AppGroup): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + group.id;
        let ret = this.httpUtils.httpDelete('删除组', url);
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.model.opDelModel.next(group.id)
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public addHost(host: Host): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/hosts/' + host.id;
        let ret = this.httpUtils.httpPost('向分组添加主机', url, '');
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opAddHostToCurrentGroup.next(host);
                    this.hostsSvc.model.opDelModel.next(host.id);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public removeHost(host: Host): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/hosts/' + host.id;
        let ret = this.httpUtils.httpDelete('从分组中移除主机', url);
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opRemoveHostFromCurrentGroup.next(host);
                    this.hostsSvc.model.opSetModel.next(host);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public addMember(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/users/' + user.id;
        let ret = this.httpUtils.httpPost('向分组添加成员', url, '');
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opAddMemberToCurrentGroup.next(user);
                    this.usersSvc.usersModel.opDelModel.next(user.id);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public removeMember(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/users/' + user.id;
        let ret = this.httpUtils.httpDelete('从分组中移除成员', url);
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opRemoveMemberFromCurrentGroup.next(user);
                    this.usersSvc.usersModel.opSetModel.next(user);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public addApp(app: App): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/apps/' + app.id;
        let ret = this.httpUtils.httpPost('向分组添加应用', url, '');
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opAddAppToCurrentGroup.next(app);
                    this.appsSvc.appsModel.opDelModel.next(app.id);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public removeApp(app: App): Observable<boolean> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/apps/' + app.id;
        let ret = this.httpUtils.httpDelete('从分组中移除应用', url);
        return ret.pipe(map(
            data => {
                if (data.code == 0) {
                    this.opRemoveAppFromCurrentGroup.next(app);
                    this.appsSvc.appsModel.opSetModel.next(app);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    get group(): Observable<AppGroup> {
        return this.currentGroup;
    }

    public setSelectedGroup(groupId: number) {
        this.currentGroupId = groupId;
        this.model.setSelected(groupId);
    }

    public search(text: Observable<string>, notFindedDesc: string, textList: Observable<string[]>) {
        return text.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            flatMap(term => {
                if (term.length < 2) {
                    return [];
                } else {
                    return textList.pipe(
                        map(l => {
                            if (l.length == 0) {
                                return [notFindedDesc]
                            } else {
                                return l.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10) 
                            }
                        })
                    )
                }
            })
        );
    }
}
