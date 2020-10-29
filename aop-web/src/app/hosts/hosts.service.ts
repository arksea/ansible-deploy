import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Host } from '../app.entity'
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

    public getHosts(): Observable<ServiceResponse<Array<Host>>> {
        const url = environment.apiUrl + '/api/hosts'
        return this.httpUtils.httpGet('查询主机列表', url)
    }

    public getHostsInGroup(groupId: number): Observable<ServiceResponse<Array<Host>>> {
        const url = environment.apiUrl + '/api/hosts?groupId='+groupId
        return this.httpUtils.httpGet('查询主机列表', url)
    }
}
