import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators'
import { PortSection, PortType } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'

@Injectable()
export class PortsService {

    public portTypes: Array<PortType> = []
    public portTypesMap: Map<number,PortType> = new Map()

    public constructor(private httpUtils: HttpUtils) {
        this.queryPortTypes()
    }

    //更新各类型端口使用情况的统计数据
    private queryPortTypes() {
        this.getPortTypes().subscribe(ret => {
            if (ret.code == 0) {
                this.portTypes = ret.result
                this.portTypesMap.clear()
                this.portTypes.forEach(t => this.portTypesMap[t.id]=t)
            }
        })
    }

    public savePortSection(section: PortSection): Observable<ServiceResponse<PortSection>> {
        const url = environment.apiUrl + '/api/ports/sections'
        return this.httpUtils.httpPut('保存端口区间', url, section).pipe(map(ret => {
            this.queryPortTypes()
            return ret
        }))
    }

    public getSections() {
        const url = environment.apiUrl + '/api/ports/sections'
        return this.httpUtils.httpGet('查询端口区间', url)
    }

    public deleteSection(section: PortSection): Observable<ServiceResponse<boolean>> {
        const url = environment.apiUrl + '/api/ports/sections/'+section.id
        return this.httpUtils.httpDelete('删除端口区间', url).pipe(map(ret => {
            this.queryPortTypes()
            return ret
        }))
    }

    public getPortTypes(): Observable<ServiceResponse<Array<PortType>>> {
        const url = environment.apiUrl + '/api/ports/types'
        return this.httpUtils.httpGet('查询端口类型', url)
    }
}
