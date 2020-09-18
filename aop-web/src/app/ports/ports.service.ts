import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { PortSection, PortType } from '../app.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { CrudModel, IModelInfo } from '../utils/crud-model';

class SectionModelInfo implements IModelInfo<number, PortSection> {
    public getModelMapKey(t: PortSection): number {
        return t.id;
    }
    public getSortKey(t: PortSection): any {
        return t.minValue;
    }
    public needSort(): boolean {
        return true;
    }
    public getSortOrder(): string {
        return 'asc';
    }
}

@Injectable()
export class PortsService {
    public model: CrudModel<number, PortSection> = new CrudModel<number, PortSection>(new SectionModelInfo());
    public sectionList: Subject<PortSection[]> = this.model.modelList;
    public _portTypes: Array<PortType>;
    public portTypesMap: Map<number,PortType> = new Map();

    public constructor(private httpUtils: HttpUtils, 
        private router: Router, 
        private alert: MessageNotify) {
            this.queryPortTypes();
    }

    public savePortSection(section: PortSection): Observable<boolean> {
        const url = environment.apiUrl + '/api/ports/sections';
        return this.httpUtils.httpPut('保存端口区间', url, section).pipe(
            map(response => {
                    if (response.code === 0) {
                        this.querySections();
                        return true;
                    } else {
                        return false;
                    }
                }
            )
        );
    }

    public querySections() {
        const url = environment.apiUrl + '/api/ports/sections';
        let ret: Observable<ServiceResponse<Array<PortSection>>> = this.httpUtils.httpGet('查询端口区间', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this.model.opResetModels.next(data.result);
            }
        });
    }

    public deleteSection(section: PortSection): Observable<boolean> {
        const url = environment.apiUrl + '/api/ports/sections/'+section.id;
        let ret: Observable<ServiceResponse<boolean>> = this.httpUtils.httpDelete('删除端口区间', url);
        return ret.pipe(map(data => {
            if (data.code == 0) {
                this.model.opDelModel.next(section.id);
                return true;
            } else {
                return false;
            }
        }));
    }

    private queryPortTypes() {
        const url = environment.apiUrl + '/api/ports/types';
        let ret: Observable<ServiceResponse<Array<PortType>>> = this.httpUtils.httpGet('查询端口类型', url);
        ret.subscribe(data => {
            if (data.code == 0) {
                this._portTypes = data.result;
                this._portTypes.forEach(t => this.portTypesMap[t.id]=t);
            }
        });
    }

    get portTypes() {
        return this._portTypes;
    }
}
