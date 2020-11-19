import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Host, HostsPage } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'

@Injectable()
export class HostsService {

    public constructor(private httpUtils: HttpUtils) {
    }

    public saveHost(host: Host): Observable<ServiceResponse<number>> {
        const url = environment.apiUrl + '/api/hosts'
        return this.httpUtils.httpPost('保存主机信息', url, host)
    }

    public deleteHost(hostId: number): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/hosts/' + hostId
        return this.httpUtils.httpDelete('删除主机', url)
    }

    public getHosts(page: number, pageSize: number, groupId: number, ipSearch: string): Observable<ServiceResponse<HostsPage>> {
        let url = environment.apiUrl + '/api/hosts?page='+page+'&pageSize='+pageSize
        if (groupId != null && groupId != undefined) {
            url = url + '&groupId='+groupId
        }
        if (ipSearch != '') {
            url = url + '&ipSearch='+ipSearch
        }
        return this.httpUtils.httpGet('查询主机列表', url)
    }

    public getHostsInGroup(groupId: number): Observable<ServiceResponse<Array<Host>>> {
        const url = environment.apiUrl + '/api/hosts?groupId='+groupId
        return this.httpUtils.httpGet('查询主机列表', url)
    }
}
