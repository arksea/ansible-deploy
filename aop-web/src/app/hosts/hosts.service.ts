import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Host, Page, AppGroup } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'

@Injectable()
export class HostsService {

    public constructor(private httpUtils: HttpUtils) {
    }

    public saveHost(host: Host): Observable<ServiceResponse<Host>> {
        const url = environment.apiUrl + '/api/hosts'
        return this.httpUtils.httpPost('保存主机信息', url, host)
    }

    public batchAddHosts(ipRange: string, descPrefix: string, groupId: number): Observable<ServiceResponse<Host[]>> {
        const url = environment.apiUrl + '/api/hosts/batch?ipRange='+ipRange
            +'&descPrefix='+descPrefix+'&groupId='+groupId
        return this.httpUtils.httpPost('保存主机信息', url, '')
    }

    public deleteHost(hostId: number): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/hosts/' + hostId
        return this.httpUtils.httpDelete('删除主机', url)
    }

    public getHosts(page: number, pageSize: number, ipSearch: string): Observable<ServiceResponse<Page<Host>>> {
        let url = environment.apiUrl + '/api/hosts?page='+page+'&pageSize='+pageSize
        if (ipSearch != '') {
            url = url + '&ipSearch='+ipSearch
        }
        return this.httpUtils.httpGet('查询主机列表', url)
    }

    public getHostsInGroup(page: number, pageSize: number, ipSearch: string, groupId: number): Observable<ServiceResponse<Page<Host>>> {
        let url = environment.apiUrl + '/api/hosts?page='+page+'&pageSize='+pageSize
        if (groupId != null && groupId != undefined) {
            url = url + '&groupId='+groupId
        }
        if (ipSearch != '') {
            url = url + '&ipSearch='+ipSearch
        }
        return this.httpUtils.httpGet('查询主机列表', url)
    }

    public getGroups(): Observable<ServiceResponse<Array<AppGroup>>> {
        const url = environment.apiUrl + '/api/groups'
        return this.httpUtils.httpGet('查询组信息', url)
    }
}
