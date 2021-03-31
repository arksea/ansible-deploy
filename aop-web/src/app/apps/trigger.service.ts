import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { OperationTrigger } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'

@Injectable()
export class TriggerService {

    public constructor(private httpUtils: HttpUtils) {
    }

    public saveTrigger(trigger: OperationTrigger): Observable<ServiceResponse<OperationTrigger>> {
        const url = environment.apiUrl + '/api/triggers'
        return this.httpUtils.httpPost('保存触发器', url, trigger)
    }

    public deleteTrigger(triggerId: number): Observable<ServiceResponse<any>> {
        const url = environment.apiUrl + '/api/triggers/' + triggerId
        return this.httpUtils.httpDelete('删除触发器', url)
    }

}
