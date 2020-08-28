import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App, AppGroup } from '../app.entity';
import { AccountService } from '../account/account.service';
import { ActivatedRoute } from '@angular/router';
import { map,publishReplay,refCount } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';


@Component({
    selector: 'app-edit',
    templateUrl: './app-edit.component.html'
})
export class AppEditComponent implements OnInit {

    public app: BehaviorSubject<App> = new BehaviorSubject(new App());
    public deployPathAddon: BehaviorSubject<string> = new BehaviorSubject('');
    public appForm: BehaviorSubject<FormGroup> = new BehaviorSubject(new FormGroup({}));

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.paramMap.pipe(
            map(params => {
                let param = params.get('id');
                let app = this.getAppByRouteParam(param);
                this.initAddon(app);
                this.app.next(app);
                this.initFormGroup(app);
            }
        ),
        publishReplay(1),
        refCount()).subscribe()
    }

    public save(event: FormDataEvent) {
        event.preventDefault(); //防止刷新页面
        this.appForm.subscribe(
            f => {
                let app = new App();
                app.apptag = f.get('apptag').value;
                app.apptype = 'tomcat';
                app.deployPath = f.get('deployPath').value;
                app.description = f.get('description').value;
                let g = new AppGroup();
                g.id = 1;
                g.name = "天气";
                app.appGroup = g;
                app.enableJmx = f.get('enableJmx').value;
                this.svc.saveApp(app);
            }
        )

    }

    public cancel() {

    }

    private getAppByRouteParam(param: string): App {
        if (param == 'new') {
            return this.svc.newTomcatApp()
        } else {
            let id = Number(param);
            return new App();
        }
    }
    private initFormGroup(app: App) {
        let tag = new FormControl({value: app.apptag, disabled: app.id},[Validators.required, Validators.minLength(4), Validators.maxLength(30)]);
        let deployPath = new FormControl({value:app.deployPath, disabled: app.id},[Validators.minLength(4), Validators.maxLength(128)]);
        tag.valueChanges.subscribe(v => deployPath.setValue(v)) //deployPath建议设置为应用短名，所以这里做了变更关联
        let form = new FormGroup({
            'apptag': tag,
            'deployPath': deployPath,
            'enableJmx': new FormControl(app.enableJmx),
            'description': new FormControl(app.description,[Validators.required, Validators.minLength(5), Validators.maxLength(256)])
        })
        for (let v of app.vars) {
            form.addControl('var_'+v.name, new FormControl({value:v.value,disabled: v.isPort},[Validators.maxLength(128)]))
        }
        this.appForm.next(form);
    }

    private initAddon(app: App) {
        let pathAddon;
        switch(app.apptype) {
            case 'tomcat':
                pathAddon = '$HOME/tomcat/webapps/';
                break;
            default:
                pathAddon = '$HOME/';
                break;
        }
        this.deployPathAddon.next(pathAddon);
    }

    get apptag() : Observable<AbstractControl>    { return this.appForm.pipe(map(a => a.get('apptag'))); }
    get deployPath() : Observable<AbstractControl>{ return this.appForm.pipe(map(a => a.get('deployPath'))); }
    get description() : Observable<AbstractControl> { return this.appForm.pipe(map(a => a.get('description'))); }
}
