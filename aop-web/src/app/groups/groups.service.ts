import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { AppGroup } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { CrudModel, IModelInfo } from '../utils/crud-model';
import { debounceTime, distinctUntilChanged, map, flatMap } from 'rxjs/operators';
import { HostsService } from '../hosts/hosts.service';
import { Host } from '../app.entity';
import { IModelMapOperation, ModelData } from '../utils/crud-model';
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

    public constructor(private httpUtils: HttpUtils, 
            private router: Router, 
            private alert: MessageNotify,
            private hostsSvc: HostsService) {
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
        const url = environment.apiUrl + '/api/groups';
        let ret: Observable<ServiceResponse<Array<AppGroup>>> = this.httpUtils.httpGet('查询组信息', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.model.opResetModels.next(data.result);
            }
        });
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

    get group(): Observable<AppGroup> {
        return this.currentGroup;
    }

    public setSelectedGroup(groupId: number) {
        this.currentGroupId = groupId;
        this.model.opSetSelected.next(groupId);
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
