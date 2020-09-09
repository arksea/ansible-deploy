import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { AppGroup } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { CrudModel, IModelInfo } from '../utils/crud-model';
import { debounceTime,distinctUntilChanged,map } from 'rxjs/operators';
import { HostsService } from '../hosts/hosts.service';

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


    hosts: string[] = ['xiaohaixing','liuyawen','fengbin'];

    searchHost = (text: Observable<string>) => {
        text.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            map( term => {
                if (term.length < 2) {
                    return []
                } else {
                    return this.hosts.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10);
                }
            })
    )}

    public constructor(private httpUtils: HttpUtils, private router: Router, private alert: MessageNotify) {
    }

    public createGroup(name: string, description: string): Observable<string> {
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
                        return null;
                    } else {
                        return response.error;
                    }
                }
            )
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
        return ret.pipe(map (
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

    get group(): Observable<AppGroup> {
        return this.currentGroup;
    }

    public setSelectedGroup(groupId: number) {
        this.model.opSetSelected.next(groupId);
    }
}
