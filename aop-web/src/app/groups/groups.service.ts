import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { AppGroup } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'
import { debounceTime, distinctUntilChanged, map, flatMap } from 'rxjs/operators'
import { Host, App } from '../app.entity'
import { User } from '../users/users.entity'

@Injectable()
export class GroupsService {

    private currentGroupId: number = undefined

    public constructor(private httpUtils: HttpUtils) {
    }

    public createGroup(name: string, description: string): Observable<ServiceResponse<number>> {
        let n = encodeURI(name)
        let d = encodeURI(description)
        const url = environment.apiUrl + '/api/groups?name=' + n + '&desc=' + d
        return this.httpUtils.httpPost('新建组', url, '')
    }

    public modifyGroup(groupId: number, name: string, description: string): Observable<ServiceResponse<number>> {
        let n = encodeURI(name)
        let d = encodeURI(description)
        const url = environment.apiUrl + '/api/groups/'+groupId+'?name=' + n + '&desc=' + d
        return this.httpUtils.httpPut('新建组', url, '')
    }

    public getAppsNotInGroup(): Observable<ServiceResponse<Array<App>>> {
        let url = environment.apiUrl + '/api/apps/notInGroup'
        return this.httpUtils.httpGet('查询未分组应用', url)
    }

    public getGroups(): Observable<ServiceResponse<Array<AppGroup>>> {
        const url = environment.apiUrl + '/api/groups'
        return this.httpUtils.httpGet('查询组信息', url)
    }

    public getCurrentGroup(): Observable<ServiceResponse<AppGroup>> {
        const url = environment.apiUrl + '/api/groups/'+this.currentGroupId
        return this.httpUtils.httpGet('查询组信息', url)
    }

    public deleteGroup(group: AppGroup): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + group.id
        return this.httpUtils.httpDelete('删除组', url)
    }

    public addHost(host: Host): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/hosts/' + host.id
        return this.httpUtils.httpPost('向分组添加主机', url, '')
    }

    public removeHost(host: Host): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/hosts/' + host.id
        return this.httpUtils.httpDelete('从分组中移除主机', url)
    }

    public addMember(user: User): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/users/' + user.id
        return this.httpUtils.httpPost('向分组添加成员', url, '')
    }

    public removeMember(user: User): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/users/' + user.id
        return this.httpUtils.httpDelete('从分组中移除成员', url)
    }

    public addApp(app: App): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/apps/' + app.id
        return this.httpUtils.httpPost('向分组添加应用', url, '')
    }

    public removeApp(app: App): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/groups/' + this.currentGroupId + '/apps/' + app.id
        return this.httpUtils.httpDelete('从分组中移除应用', url)
    }

    public getHostsNotInGroup(): Observable<ServiceResponse<Array<Host>>> {
        const url = environment.apiUrl + '/api/hosts/notInGroup'
        return this.httpUtils.httpGet('查询主机列表', url)
    }

    public getUsersNotInCurrentGroup(): Observable<ServiceResponse<Array<User>>> {
        const url = environment.apiUrl + '/api/users/notInGroup/' + this.currentGroupId
        return this.httpUtils.httpGet('获取成员列表', url)
    }

    public setSelectedGroup(groupId: number) {
        this.currentGroupId = groupId
    }

    public getUserByName(name: string): Observable<ServiceResponse<User>> {
        let url = environment.apiUrl + '/api/user?name=' + name;
        return this.httpUtils.httpGet('查询用户信息', url);
    }

    public search(text: Observable<string>, textList: Observable<string[]>) {
        return text.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            flatMap(term => {
                if (term.length < 2) {
                    return []
                } else {
                    return textList.pipe(
                        map(l => {
                            if (l.length == 0) {
                                return []
                            } else {
                                return l.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10) 
                            }
                        })
                    )
                }
            })
        )
    }
}
