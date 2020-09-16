import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App } from '../app.entity';
import { AccountService } from '../account/account.service';
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
    app: App;
    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private modal: NgbModal,
                private alert: MessageNotify) {
        let appId = Number(this.route.snapshot.paramMap.get('id'));
        this.svc.getAppById(appId).subscribe(a => {
            if (a == null) {
                this.alert.warning("应用不存在或无权限(id="+appId+")");
                this.router.navigate(["/apps"]);
            } else {
                this.app = a;
            }
        });
    }

    ngOnInit() {}

    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog);
        ref.componentInstance.app = this.app;
    }

    onEditBtnClick() {

    }

    onDelBtnClick() {

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
}