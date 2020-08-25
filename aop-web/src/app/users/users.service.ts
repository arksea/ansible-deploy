import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { User } from './users.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { map, flatMap } from 'rxjs/operators';
import { CrudModel, IModelInfo, UpdateModelField } from '../utils/crud-model';
import { AddModel } from '../utils/crud-model';

// type ChildPermMap = Map<string, Set<string>>;

class UsersModelInfo implements IModelInfo<number, User> {
    private sortType: string;
    private sortOrder: string; //正序asc或逆序desc
    public getModelMapKey(t: User): number {
        return t.id;
    }
    public getSortKey(t: User): any {
        if (this.sortType == 'name') {
            return t.name;
        } else {
            return t.name;
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


@Injectable()
export class UsersService {
    private EMETY_SET: Set<string> = new Set();
    private activeUserModelInfo: UsersModelInfo = new UsersModelInfo();
    public activeUsersModel: CrudModel<number, User> = new CrudModel<number, User>(this.activeUserModelInfo);
    public activeUserList: Subject<User[]> = this.activeUsersModel.modelList;

    public constructor(private httpUtils: HttpUtils, private router: Router, private alert: MessageNotify) {
    }

    public setActiveUsersSort(type: string,order: string) {
        this.activeUserModelInfo.setSortType(type, order);
        this.activeUsersModel.opUpdateSort.next(true);
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

    public getUsers(type: string) {
        let url = environment.apiUrl + '/api/users/' + type;
        let ret: Observable<ServiceResponse<Array<User>>> = this.httpUtils.httpGet('查询用户信息', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.activeUsersModel.opResetModels.next(data.result);
            }
        });
    }

    public blockUser(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/users/active/' + user.id;
        let ret = this.httpUtils.httpDelete('禁用账号', url);
        return ret.pipe(map (
            data => {
                if (data.code == 0) {
                    this.activeUsersModel.opDelModel.next(user.id)
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public deleteUser(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/users/blocked/' + user.id;
        let ret = this.httpUtils.httpDelete('删除用户', url);
        return ret.pipe(map (
            data => {
                if (data.code == 0) {
                    this.activeUsersModel.opDelModel.next(user.id)
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }
}
