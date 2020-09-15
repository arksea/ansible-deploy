import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App, AppGroup, GroupVar } from '../app.entity';
import { AccountService } from '../account/account.service';
import { map, flatMap, publishReplay, refCount } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { format } from 'url';
import { NewVersionDialog } from './new-version.dialog';
import { HostsService } from '../hosts/hosts.service';
import { AddHostDialog } from './add-host.dialog';
import { Version } from '../app.entity';


@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    public svnaddr = 'svn://127.0.0.1';
    appId: number = undefined;
    groupId: number = undefined;
    app: App;
    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private modal: NgbModal,
                private alert: MessageNotify) {
        this.appId = Number(this.route.snapshot.paramMap.get('id'));
        this.svc.getAppById(this.appId).subscribe(a => {
            if (a == null) {
                this.alert.warning("没有查询到应用("+this.appId+")，已跳转到应用列表");
                this.router.navigate(["/apps"]);
            } else {
                this.groupId = a.appGroupId;
                this.app = a;
            }
        });
    }

    ngOnInit() {}

    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog);
        ref.componentInstance.appId = this.appId;
        ref.componentInstance.app = this.app;
    }

    onEditBtnClick() {

    }

    onDelBtnClick() {

    }

    onAddHostBtnClick(version: Version) {
        if (this.groupId) {
            this.hostSvc.getHostsInGroup(this.groupId).subscribe(
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
}