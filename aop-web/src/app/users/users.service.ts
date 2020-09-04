import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { User } from './users.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';

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

class SortType {
    type: string;
    order: string;
    desc: string;
}

@Injectable()
export class UsersService {
    private EMETY_SET: Set<string> = new Set();
    private userModelInfo: UsersModelInfo = new UsersModelInfo();
    public usersModel: CrudModel<number, User> = new CrudModel<number, User>(this.userModelInfo);
    public userList: Subject<User[]> = this.usersModel.modelList;
    public sortTypes: Array<SortType> = [{type:"name", order:"asc", desc:"用户名-正序"},
                                         {type:"name", order:"desc",desc:"用户名-逆序"}]
    public selectedSortType: BehaviorSubject<SortType> = new BehaviorSubject(this.sortTypes[1])

    public constructor(private httpUtils: HttpUtils) {
    }

    public getSortDesc(index: number): string {
        let cfg = this.sortTypes[index];
        return cfg.desc;
    }

    public setSortType(cfg) {
        this.selectedSortType.next(cfg);
        this.userModelInfo.setSortType(cfg.type, cfg.order);
        this.usersModel.opUpdateSort.next(true);        
    }

    public getUsers(type: string) {
        let url = environment.apiUrl + '/api/users/' + type;
        let ret: Observable<ServiceResponse<Array<User>>> = this.httpUtils.httpGet('查询用户信息', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.usersModel.opResetModels.next(data.result);
            }
        });
    }

    public blockUser(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/users/active/' + user.id;
        let ret = this.httpUtils.httpDelete('禁用账号', url);
        return ret.pipe(map (
            data => {
                if (data.code == 0) {
                    this.usersModel.opDelModel.next(user.id);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }

    public unblockUser(user: User): Observable<boolean> {
        const url = environment.apiUrl + '/api/users/blocked/' + user.id + '?action=unblock';
        let ret = this.httpUtils.httpPut('启用账号', url, '');
        return ret.pipe(map (
            data => {
                if (data.code == 0) {
                    this.usersModel.opDelModel.next(user.id);
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
                    this.usersModel.opDelModel.next(user.id);
                    return true;
                } else {
                    return false;
                }
            }
        ));
    }
}
