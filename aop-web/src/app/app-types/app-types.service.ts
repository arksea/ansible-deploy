import { Injectable } from '@angular/core'
import { Subject, BehaviorSubject, Observable } from 'rxjs'
import { App, AppGroup, AppType, OperationJob } from '../app.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { environment } from '../../environments/environment'
import { map, first } from 'rxjs/operators'
import { CrudModel, IModelInfo } from '../utils/crud-model'
import { Version, AppVarDefine, AppOperation, Host } from '../app.entity'

@Injectable()
export class AppTypesService {

    public constructor(private httpUtils: HttpUtils) {
    }
}
