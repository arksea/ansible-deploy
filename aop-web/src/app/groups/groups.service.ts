import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { AppGroup } from './groups.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';

// type ChildPermMap = Map<string, Set<string>>;

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
                        this.model.opAddModel.next({key: response.result, value: group});
                        return null;
                    } else {
                        return response.error;
                    }
                }
            )
        );
    }

    public getGroups() {
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
}
