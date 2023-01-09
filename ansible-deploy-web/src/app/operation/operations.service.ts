import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppOperation, AppOperationCode } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { environment } from '../../environments/environment';

@Injectable()
export class OperationsService {

    public constructor(private httpUtils: HttpUtils) {
    }

    public saveOperation(operation: AppOperation): Observable<ServiceResponse<AppOperation>> {
        const url = environment.apiUrl + '/api/operations'
        return this.httpUtils.httpPost('保存操作脚本', url, operation)
    }

    public deleteOperationCode(code: AppOperationCode):Observable<ServiceResponse<boolean>> {
        const url = environment.apiUrl + '/api/operations/codes/' + code.id
        return this.httpUtils.httpDelete('删除操作脚本', url)
    }

    public getOperationById(id: number): Observable<ServiceResponse<AppOperation>> {
        const url = environment.apiUrl + '/api/operations/' + id
        return this.httpUtils.httpGet('查询操作', url)
    }
}
