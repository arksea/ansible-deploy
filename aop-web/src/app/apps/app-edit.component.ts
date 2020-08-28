import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App } from '../app.entity';
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

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private route: ActivatedRoute) {
    }

    appForm: BehaviorSubject<FormGroup> = new BehaviorSubject(new FormGroup({}));

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

    private getAppByRouteParam(param: string): App {
        if (param == 'new') {
            return this.svc.newTomcatApp()
        } else {
            let id = Number(param);
            return new App();
        }
    }
    private initFormGroup(app: App) {
        let form = new FormGroup({
            'apptag': new FormControl(app.apptag,[Validators.required, Validators.minLength(4), Validators.maxLength(30)]),
            'deployPath': new FormControl(app.deployPath,[Validators.minLength(4), Validators.maxLength(128)]),
            'enableJmx': new FormControl(app.enableJmx),
            'description': new FormControl(app.describes,[Validators.required, Validators.minLength(5), Validators.maxLength(256)])
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
