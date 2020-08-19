import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { scan, map, publishReplay, refCount } from 'rxjs/operators';
import * as _ from 'lodash';

export type ModelMap<K,T> = Map<K,T>;

export type IModelMapOperation<K,T> = (map: ModelMap<K,T>) => ModelMap<K,T>;

export interface UpdateModelField<K,F> {
    key: K;
    fieldValue: F;
}

export interface AddModel<K,T> {
    key: K;
    value: T;
}

export interface IModelInfo<K,T> {
    getModelMapKey(t: T): K;
    getSortKey(t: T): any;
    needSort(): boolean;
}

export class CrudModel<K,T> {
    // opUpdateModels     ──┬──＞ updates  ──＞ modelMap ──＞ modelList
    // opUpdateModel      ──┤
    // opResetModels      ──┤
    // opAddModel         ──┤
    // opDelModel         ──┘
    opResetModels: Subject<T[]> = new Subject();
    opUpdateModels: Subject<T[]> = new Subject();
    opUpdateModel: Subject<T> = new Subject();
    opAddModel: Subject<AddModel<K,T>> = new Subject();
    opDelModel: Subject<K> = new Subject();
    updates: Subject<IModelMapOperation<K,T>> = new Subject();
    public modelMap: Observable<ModelMap<K,T>>;
    public modelList: Subject<T[]> = new BehaviorSubject<T[]>([]);

    public constructor(mapping: IModelInfo<K,T>) {
        //订阅更新操作
        this.modelMap = this.updates.pipe(
            scan((modelMap: ModelMap<K,T>, op: IModelMapOperation<K,T>) => {
                const newMap = op(modelMap);
                return newMap;
            }, new Map()),
            publishReplay(1),
            refCount()
        );
        this.opResetModels.pipe(
            map(function (values: T[]): IModelMapOperation<K,T> {
                return (oldMap: ModelMap<K,T>) => {
                    let newMap = new Map();
                    values.forEach(it => newMap.set(mapping.getModelMapKey(it), it));
                    return newMap;
                }
            })
        ).subscribe(this.updates);

        this.opUpdateModels.pipe(
            map(function (values: T[]): IModelMapOperation<K,T> {
                return (modelMap: ModelMap<K,T>) => {
                    values.forEach(it => modelMap.set(mapping.getModelMapKey(it), it));
                    return modelMap;
                }
            })
        ).subscribe(this.updates);

        this.opUpdateModel.pipe(
            map(function (value: T): IModelMapOperation<K,T> {
                return (modelMap: ModelMap<K,T>) => {
                    modelMap.set(mapping.getModelMapKey(value), value);
                    return modelMap;
                }
            })
        ).subscribe(this.updates);

        // 生成排序的列表
        this.modelMap.pipe(
            map((modelMap: ModelMap<K,T>) => {
                const list: T[] = [];
                modelMap.forEach((it, key) => {
                    list.push(it);
                });
                if (mapping.needSort()) {
                    return _.sortBy(list, (t: T) => mapping.getSortKey(t));
                } else {
                    return list;
                }
            })
        ).subscribe(this.modelList);

        this.opAddModel.pipe(
            map(function (add: AddModel<K,T>): IModelMapOperation<K,T> {
                return (modelMap: ModelMap<K,T>) => {
                    modelMap.set(add.key, add.value);
                    return modelMap;
                };
            })
        ).subscribe(this.updates);

        this.opDelModel.pipe(
            map(function (key: K): IModelMapOperation<K,T> {
                return (modelMap: ModelMap<K,T>) => {
                    modelMap.delete(key);
                    return modelMap;
                };
            })
        ).subscribe(this.updates);
    }
}