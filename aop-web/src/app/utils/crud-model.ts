import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { scan, map, publishReplay, refCount } from 'rxjs/operators';
import * as _ from 'lodash';

export type ModelMap<K,T> = Map<K,T>;

export type IModelMapOperation<K,T> = (data: ModelData<K,T>) => ModelData<K,T>;

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
    getSortOrder(): string;
}

export class ModelData<K,T> {
    constructor(public map: ModelMap<K,T>,public selected: K) {
    };
}

export class CrudModel<K,T> {
    // opUpdateModels     ──┬──＞ updates  ===＞ modelMap ──＞ modelList
    // opSetModel         ──┤
    // opResetModels      ──┤
    // opDelModel         ──┤
    // opUpdateSort       ──┘
    defaultValue: T;
    opResetModels: Subject<T[]> = new Subject();
    opUpdateModels: Subject<T[]> = new Subject();
    opSetModel: Subject<T> = new Subject();
    opDelModel: Subject<K> = new Subject();
    opUpdateSort: Subject<any> = new Subject();
    opSetSelected: Subject<K> = new Subject();
    updates: Subject<IModelMapOperation<K,T>> = new Subject();
    public modelData: Observable<ModelData<K,T>>;
    public modelList: Subject<T[]> = new BehaviorSubject<T[]>([]);
    public modelSelected: Subject<T> = new BehaviorSubject<T>(undefined);

    public constructor(mapping: IModelInfo<K,T>, defaultValue: T) {
        this.defaultValue = defaultValue;
        this.modelSelected = new BehaviorSubject<T>(defaultValue);
        //订阅更新操作
        this.modelData = this.updates.pipe(
            scan((modelData: ModelData<K,T>, op: IModelMapOperation<K,T>) => {
                const newData = op(modelData);
                return newData;
            }, new ModelData(new Map(),undefined)),
            publishReplay(1),
            refCount()
        );
        this.opResetModels.pipe(
            map(function (values: T[]): IModelMapOperation<K,T> {
                return (oldData: ModelData<K,T>) => {
                    let newData = new ModelData(new Map(), oldData.selected);
                    values.forEach(it => newData.map.set(mapping.getModelMapKey(it), it));
                    return newData;
                }
            })
        ).subscribe(this.updates);

        this.opUpdateModels.pipe(
            map(function (values: T[]): IModelMapOperation<K,T> {
                return (modelData: ModelData<K,T>) => {
                    values.forEach(it => modelData.map.set(mapping.getModelMapKey(it), it));
                    return modelData;
                }
            })
        ).subscribe(this.updates);

        this.opSetModel.pipe(
            map(function (value: T): IModelMapOperation<K,T> {
                return (modelData: ModelData<K,T>) => {
                    modelData.map.set(mapping.getModelMapKey(value), value);
                    return modelData;
                }
            })
        ).subscribe(this.updates);

        this.opSetSelected.pipe(
            map(function (value: K): IModelMapOperation<K,T> {
                return (modelData: ModelData<K,T>) => {
                    modelData.selected = value;
                    return modelData;
                }
            })
        ).subscribe(this.updates);

        this.opDelModel.pipe(
            map(function (key: K): IModelMapOperation<K,T> {
                return (modelData: ModelData<K,T>) => {
                    modelData.map.delete(key);
                    return modelData;
                };
            })
        ).subscribe(this.updates);

        this.opUpdateSort.pipe(
            map(function (_: any): IModelMapOperation<K,T> {
                return (modelData: ModelData<K,T>) => {
                    return modelData;
                };
            })
        ).subscribe(this.updates);


        // 生成排序的列表
        this.modelData.pipe(
            map((modelData: ModelData<K,T>) => {
                const list: T[] = [];
                modelData.map.forEach((it, key) => {
                    list.push(it);
                });
                if (mapping.needSort()) {
                    return _.orderBy(list, (t: T) => mapping.getSortKey(t), [mapping.getSortOrder()]);
                } else {
                    return list;
                }
            })
        ).subscribe(this.modelList);

        //
        this.modelData.pipe(
            map((modelData: ModelData<K,T>) => {
                if (modelData.selected == undefined) {
                    return this.defaultValue;
                } else {
                    return modelData.map.get(modelData.selected);
                }
            })
        ).subscribe(this.modelSelected);
    }
}