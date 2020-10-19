import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable, of, BehaviorSubject } from 'rxjs';
import { map, first, flatMap } from 'rxjs/operators';
import { AppOperation, AppType, AppOperationCode } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { CrudModel, IModelInfo } from '../utils/crud-model';


class HostsModelInfo implements IModelInfo<number, AppOperation> {
    public getModelMapKey(t: AppOperation): number {
        return t.id;
    }
    public getSortKey(t: AppOperation): any {
        return t.id;
    }
    public needSort(): boolean {
        return true;
    }
    public getSortOrder(): string {
        return 'asc';
    }
}

@Injectable()
export class OperationsService {

    public model: CrudModel<number, AppOperation> = new CrudModel<number, AppOperation>(new HostsModelInfo());
    public operationList: Subject<Array<AppOperation>> = this.model.modelList;
    public appTypes: Array<AppType>;
    public appTypesMap: Map<number,AppType> = new Map();
    public appType: AppType;

    public constructor(private httpUtils: HttpUtils, 
            private router: Router, 
            private alert: MessageNotify) {
        this.queryAppTypes();
    }

    public queryAppTypes() {
        const url = environment.apiUrl + '/api/appTypes';
        let ret: Observable<ServiceResponse<Array<AppType>>> = this.httpUtils.httpGet('查询应用类型', url);
        ret.subscribe(ret => {
            if (ret.code == 0) {
                this.appTypes = ret.result;
                for (let t of this.appTypes) {
                    this.appTypesMap[t.id] = t;
                }
                if (this.appType == undefined) {
                    this.appType = this.appTypes[0];
                }
                this.queryOperations();
            }
        });
    }

    public saveOperation(operation: AppOperation): Observable<ServiceResponse<AppOperation>> {
        const url = environment.apiUrl + '/api/operations';
        return this.httpUtils.httpPost('保存操作脚本', url, operation);
    }

    public queryOperations() {
        const url = environment.apiUrl + '/api/operations/?appTypeId=' + this.appType.id;
        this.httpUtils.httpGet('查询操作', url).subscribe(ret => {
            if (ret.code == 0) {
                this.model.opResetModels.next(ret.result)
            }
        });
    }

    public getOperationById(id: number): Observable<AppOperation> {
        return this.model.modelData.pipe(first(),flatMap(data => { 
            let op = data.map.get(id)
            if (op) {
                return new BehaviorSubject(op);
            } else {
                const url = environment.apiUrl + '/api/operations/' + id;
                return this.httpUtils.httpGet('查询操作脚本', url).pipe(map(ret => {
                    if (ret.code == 0) {
                        return ret.result;
                    } else {
                        return null;
                    }
                }));
            }
        }))
    }

    public setSelectedAppType(appType: AppType) {
        this.appType = appType;
        this.queryOperations();
    }

    public deleteOperation(operation: AppOperation): Observable<boolean> {
        const url = environment.apiUrl + '/api/operations/' + operation.id;
        return this.httpUtils.httpDelete('删除操作', url).pipe(map(ret => {
            if (ret.code == 0) {
                this.model.opDelModel.next(operation.id);
                return true;
            } else {
                return false;
            }
        }));
    }

    public deleteOperationCode(code: AppOperationCode): Observable<boolean> {
        const url = environment.apiUrl + '/api/operations/codes/' + code.id;
        return this.httpUtils.httpDelete('删除操作脚本', url).pipe(map(ret => {
            return ret.code == 0;
        }));
    }
}
