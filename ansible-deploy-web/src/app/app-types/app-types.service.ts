import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { AppType, PortType, AppOperation } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'

@Injectable()
export class AppTypesService {

    public constructor(private httpUtils: HttpUtils) {
    }

    public getAppType(typeId: number): Observable<ServiceResponse<AppType>> {
        const url = environment.apiUrl + '/api/appTypes/'+typeId;
        return this.httpUtils.httpGet('查询应用类型', url);
    }

    public getAppTypes(): Observable<ServiceResponse<Array<AppType>>> {
        const url = environment.apiUrl + '/api/appTypes';
        return this.httpUtils.httpGet('查询应用类型', url);
    }

    public deleteAppType(id: number): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/appTypes/'+id;
        return this.httpUtils.httpDelete('查询应用类型', url);
    }

    public saveAppType(appType: AppType): Observable<ServiceResponse<AppType>> {
        const url = environment.apiUrl + '/api/appTypes';
        return this.httpUtils.httpPost('保存应用类型', url, appType);
    }

    public getPortTypes(): Observable<ServiceResponse<Array<PortType>>> {
        const url = environment.apiUrl + '/api/ports/types';
        return this.httpUtils.httpGet('查询端口类型', url);
    }

    public getOperations(appTypeId: number): Observable<ServiceResponse<Array<AppOperation>>> {
        const url = environment.apiUrl + '/api/appTypes/'+appTypeId + '/operations'
        return this.httpUtils.httpGet('查询操作', url)
    }

    public deleteOperation(operation: AppOperation): Observable<ServiceResponse<boolean>> {
        const url = environment.apiUrl + '/api/operations/' + operation.id
        return this.httpUtils.httpDelete('删除操作', url)
    }
}
