import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { App, AppGroup } from '../app.entity';
import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms';
import { AppsService } from './apps.service';
import { scan, map, publishReplay, refCount } from 'rxjs/operators';
import { MessageNotify } from '../utils/message-notify';
import { Router } from '@angular/router';

class AppEditModel {
    constructor(public app: App, public appForm: FormGroup, public deployPathAddon: string) {}
}

export type IAppEditModelOperation = (data: AppEditModel) => AppEditModel;

@Injectable()
export class AppEditService {
    //动态表单设置（不同的应用类型有不同的表单）
    //AppsService.app  ───>  opSetApp     ──┬──> updates  ───> model ──┬─> appForm
    //                                    ──┤                          ├─> apptag
    //                       opSaveApp    ──┘                          ├─> deployPath
    //                                                                 ├─> description
    //                                                                 └─> deployPathAddon
    private updates: Subject<IAppEditModelOperation> = new Subject();
    private model: Observable<AppEditModel>;
    
    public opSetApp: Subject<App> = new Subject();
    public opSaveApp: Subject<any> = new Subject();
    public deployPathAddon: BehaviorSubject<string> = new BehaviorSubject('');
    public appForm: BehaviorSubject<FormGroup>;
    public apptag: BehaviorSubject<AbstractControl>;
    public deployPath: BehaviorSubject<AbstractControl>;
    public description: BehaviorSubject<AbstractControl>;
    public userGroups: BehaviorSubject<AbstractControl>;

    public constructor(private svc: AppsService,protected alert: MessageNotify,private router: Router) {
        let app = svc.newTomcatApp();
        let form = this.makeFormGroup(app);
        this.apptag = new BehaviorSubject(form.get('apptag'));
        this.deployPath = new BehaviorSubject(form.get('deployPath'));
        this.description = new BehaviorSubject(form.get('description'));
        this.userGroups = new BehaviorSubject(form.get('userGroups'));
        this.appForm = new BehaviorSubject(form);
        //订阅更新操作
        this.model = this.updates.pipe(
            scan((model: AppEditModel, op: IAppEditModelOperation) => {
                return op(model);
            }, new AppEditModel(app,form,'')),
            publishReplay(1),
            refCount()
        );
        let f1 = this.makeFormGroup
        let f2 = this.makeAddon
        this.opSetApp.pipe(
            map(function (app: App): IAppEditModelOperation {
                return (oldModel: AppEditModel) => {
                    if (app) {
                        let f = f1(app);
                        let addon = f2(app)
                        return new AppEditModel(app, f, addon);
                    } else {
                        return oldModel;
                    }
                }
            })
        ).subscribe(this.updates);

        this.svc.app.pipe(map(a => {
            if (a == null) {
                return this.svc.newTomcatApp();
            } else {
                return a;
            }
        })).subscribe(this.opSetApp);

        let saveFunc = this.save;
        this.opSaveApp.pipe(
            map(function (_: any): IAppEditModelOperation {
                return (model: AppEditModel) => {
                    saveFunc(model,svc,alert,router);
                    return model;
                }
            })
        ).subscribe(this.updates);
        
        this.model.pipe(map(model => model.appForm.get('apptag'))).subscribe(this.apptag);
        this.model.pipe(map(model => model.appForm.get('deployPath'))).subscribe(this.deployPath);
        this.model.pipe(map(model => model.appForm.get('description'))).subscribe(this.description);
        this.model.pipe(map(model => model.appForm.get('userGroups'))).subscribe(this.userGroups);
        this.model.pipe(map(model => model.appForm)).subscribe(this.appForm);
        this.model.pipe(map(model => model.deployPathAddon)).subscribe(this.deployPathAddon);
    }

    public makeFormGroup(app: App): FormGroup {
        let tag = new FormControl({ value: app.apptag, disabled: app.id }, [Validators.required, Validators.minLength(4), Validators.maxLength(30)]);
        let deployPath = new FormControl({ value: app.deployPath, disabled: app.id }, [Validators.minLength(4), Validators.maxLength(128)]);
        tag.valueChanges.subscribe(v => deployPath.setValue(v)) //deployPath建议设置为应用短名，所以这里做了变更关联
        let userGroups = new FormControl(app.appGroupId, [Validators.required]);
        let f =  new FormGroup({
            'userGroups': userGroups,
            'apptag': tag,
            'deployPath': deployPath,
            'enableJmx': new FormControl(app.enableJmx),
            'description': new FormControl(app.description, [Validators.required, Validators.minLength(5), Validators.maxLength(256)])
        })
        for (let v of app.vars) {
            f.addControl('var_' + v.name, new FormControl({ value: v.value, disabled: v.isPort }, [Validators.maxLength(128)]))
        }
        return f;
    }

    public makeAddon(app: App): string {
        switch (app.apptype) {
            case 'tomcat':
                return '$HOME/tomcat/webapps/';
            default:
                return '$HOME/';
        }
    }

    public save(model: AppEditModel, svc: AppsService, alert: MessageNotify, router: Router) {
        let f = model.appForm;
        let a = model.app;
        a.apptag = f.get('apptag').value;
        a.apptype = 'tomcat';
        a.deployPath = f.get('deployPath').value;
        a.description = f.get('description').value;
        a.appGroupId = Number(f.get('appGroup').value);
        a.enableJmx = f.get('enableJmx').value;
        for (let i of a.vars) {
            let c = f.get('var_' + i.name);
            i.value = c.value;
        }
        svc.saveApp(a).subscribe(
            resp => {
                if (resp.code == 0) {
                    alert.success('保存应用成功');
                    router.navigate(['/apps/' + resp.result]);
                } else {
                    alert.error('保存应用失败：' + resp.error);
                }
            }
        )
    }
}
