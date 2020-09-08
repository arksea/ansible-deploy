import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { Host } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { CrudModel, IModelInfo } from '../utils/crud-model';

// type ChildPermMap = Map<string, Set<string>>;

class HostsModelInfo implements IModelInfo<number, Host> {
    public getModelMapKey(t: Host): number {
        return t.id;
    }
    public getSortKey(t: Host): any {
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
export class HostsService {
    private EMETY_SET: Set<string> = new Set();
    public model: CrudModel<number, Host> = new CrudModel<number, Host>(new HostsModelInfo());
    public hostList: Subject<Host[]> = this.model.modelList;

    public constructor(private httpUtils: HttpUtils, private router: Router, private alert: MessageNotify) {
    }

    public saveHost(host: Host): Observable<string> {
        const url = environment.apiUrl + '/api/hosts';
        return this.httpUtils.httpPost('保存主机信息', url, host).pipe(
            map(response => {
                    if (response.code === 0) {
                        host.id = response.result;
                        host.createTime = new Date().toISOString()
                        this.model.opSetModel.next(host);
                        return null;
                    } else {
                        return response.error;
                    }
                }
            )
        );
    }

    public getHosts() {
        const url = environment.apiUrl + '/api/hosts';
        let ret: Observable<ServiceResponse<Array<Host>>> = this.httpUtils.httpGet('查询主机列表', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.model.opResetModels.next(data.result);
            }
        });
    }
}
