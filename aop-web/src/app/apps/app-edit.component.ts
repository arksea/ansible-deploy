import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App, AppGroup, GroupVar } from '../app.entity';
import { AccountService } from '../account/account.service';
import { ActivatedRoute, Router } from '@angular/router';
import { map, flatMap, publishReplay, refCount } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { format } from 'url';


@Component({
    selector: 'app-edit',
    templateUrl: './app-edit.component.html'
})
export class AppEditComponent implements OnInit {

    public app: App;
    public deployPathAddon: BehaviorSubject<string> = new BehaviorSubject('');
    public appForm: FormGroup = new FormGroup({
        apptag: new FormControl(),
        deployPath: new FormControl(),
        description: new FormControl(),
        enableJmx: new FormControl()
    });

    constructor(public svc: AppsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router) {
            this.app = this.svc.editingApp;
            this.initAddon(this.app);
            this.initFormGroup(this.app);
    }

    ngOnInit(): void {
    }

    public save(event: FormDataEvent) {
        event.preventDefault(); //取消submit事件的默认处理：刷新页面
        let f = this.appForm;
        let a = this.app;
        a.apptag = f.get('apptag').value;
        a.apptype = 'tomcat';
        a.deployPath = f.get('deployPath').value;
        a.description = f.get('description').value;
        let g = new AppGroup();
        g.id = 1;
        g.name = "天气";
        a.appGroup = g;
        a.enableJmx = f.get('enableJmx').value;
        for (let i of a.vars) {
            let c = f.get('var_' + i.name);
            i.value = c.value;
        }
        this.svc.saveApp(a).subscribe(
            resp => {
                if (resp.code == 0) {
                    this.alert.success('保存应用成功');
                    this.router.navigate(['/apps/' + resp.result]);
                } else {
                    this.alert.error('保存应用失败：' + resp.error);
                }
            }
        )
    }

    public cancel() {
        this.router.navigate(['/apps'])
    }

    private initFormGroup(app: App) {
        let tag = new FormControl({ value: app.apptag, disabled: app.id }, [Validators.required, Validators.minLength(4), Validators.maxLength(30)]);
        let deployPath = new FormControl({ value: app.deployPath, disabled: app.id }, [Validators.minLength(4), Validators.maxLength(128)]);
        tag.valueChanges.subscribe(v => deployPath.setValue(v)) //deployPath建议设置为应用短名，所以这里做了变更关联
        this.appForm = new FormGroup({
            'apptag': tag,
            'deployPath': deployPath,
            'enableJmx': new FormControl(app.enableJmx),
            'description': new FormControl(app.description, [Validators.required, Validators.minLength(5), Validators.maxLength(256)])
        })
        for (let v of app.vars) {
            this.appForm.addControl('var_' + v.name, new FormControl({ value: v.value, disabled: v.isPort }, [Validators.maxLength(128)]))
        }
    }

    private initAddon(app: App) {
        let pathAddon;
        switch (app.apptype) {
            case 'tomcat':
                pathAddon = '$HOME/tomcat/webapps/';
                break;
            default:
                pathAddon = '$HOME/';
                break;
        }
        this.deployPathAddon.next(pathAddon);
    }

    get apptag(): AbstractControl { return this.appForm.get('apptag'); }
    get deployPath(): AbstractControl { return this.appForm.get('deployPath'); }
    get description(): AbstractControl { return this.appForm.get('description'); }
}
