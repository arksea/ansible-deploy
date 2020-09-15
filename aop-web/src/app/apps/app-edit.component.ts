import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App,AppGroup } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NewVersionDialog } from './new-version.dialog';
import { HostsService } from '../hosts/hosts.service';
import { AddHostDialog } from './add-host.dialog';
import { Version } from '../app.entity';
import { ConfirmDialog } from '../utils/confirm.dialog';


@Component({
    selector: 'app-edit',
    templateUrl: './app-edit.component.html'
})
export class AppEditComponent implements OnInit {

    public groupSelectModel: any;
    public app: App;
    public appForm: FormGroup;
    public deployPathAddon: string;
    public isNewAction: boolean;
    public userGroups: Observable<AppGroup[]>;

    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.userGroups = this.svc.userGroups;
        let str = this.route.snapshot.paramMap.get('id');
        this.app = svc.newTomcatApp();
        this.appForm = this.makeFormGroup(this.app);
        this.deployPathAddon = this.makeAddon(this.app);
        if (str == 'new') {
            this.isNewAction = true;
        } else {
            this.isNewAction = false;
            let appId = Number(str);
            this.svc.getAppById(appId).subscribe(a => {
                if (a == null) {
                    this.alert.warning("没有查询到应用("+appId+")，已跳转到应用列表");
                    this.router.navigate(["/apps"]);
                } else {
                    this.app = a;
                    this.appForm = this.makeFormGroup(a);
                    this.deployPathAddon = this.makeAddon(a);
                }
            });
        }
    }

    ngOnInit(): void {
    }

    public makeFormGroup(app: App): FormGroup {
        let tag = new FormControl({ value: app.apptag, disabled: app.id }, [Validators.required, Validators.minLength(4), Validators.maxLength(30)]);
        let deployPath = new FormControl({ value: app.deployPath, disabled: app.id }, [Validators.minLength(4), Validators.maxLength(128)]);
        tag.valueChanges.subscribe(v => deployPath.setValue(v)) //deployPath建议设置为应用短名，所以这里做了变更关联
        let appGroupId = new FormControl(app.appGroupId, [Validators.required]);
        let f =  new FormGroup({
            'appGroupId': appGroupId,
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

    public save() {
        let f = this.appForm;
        let a = this.app;
        a.apptag = this.apptag.value;
        a.apptype = 'tomcat';
        a.deployPath = this.deployPath.value;
        a.description = this.desc.value;
        a.appGroupId = Number(this.appGroupId.value);
        a.enableJmx = this.enableJmx.value;
        for (let i of a.vars) {
            let c = f.get('var_' + i.name);
            i.value = c.value;
        }
        this.svc.saveApp(a).subscribe(
            id => {
                if (id) {
                    a.id = id;
                    if (this.isNewAction) {
                        this.svc.appsModel.opSetModel.next(a);
                    }
                    this.alert.success('保存应用成功');
                    this.router.navigate(['/apps']);
                }
            }
        )
    }


    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog);
        ref.componentInstance.app = this.app;
    }

    onAddHostBtnClick(version: Version) {
        if (this.app.appGroupId) {
            this.hostSvc.getHostsInGroup(this.app.appGroupId).subscribe(
                ret => {
                    if (ret.code == 0) {
                        let ref = this.modal.open(AddHostDialog);
                        ref.componentInstance.setParams(this.app, version, ret.result);
                    }
                }
            )
        } else {
            this.alert.warning("应用还未加入分组，不能配置部署主机");
        }
    }

    onEditBtnClick() {

    }

    onDeleteVersionBtnClick(version: Version) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "删除版本: "+version.name;
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
          if (result == "ok") {
            this.svc.deleteVersionById(version.id).subscribe(success => {
                if (success) {
                    this.app.versions = this.app.versions.filter((v,i,a) => v.id != version.id)
                    this.alert.success("已删除");
                }
            });
          }
        }, resaon => {})
    }

    public cancel() {
        this.router.navigate(['/apps'])
    }

    public get apptag(): AbstractControl {
        return this.appForm.get('apptag');
    }

    public get deployPath(): AbstractControl {
        return this.appForm.get('deployPath');
    }

    public get desc(): AbstractControl {
        return this.appForm.get('description');
    }

    public get appGroupId(): AbstractControl {
        return this.appForm.get('appGroupId');
    }

    public get enableJmx() : AbstractControl {
        return this.appForm.get('enableJmx');
    }

}
