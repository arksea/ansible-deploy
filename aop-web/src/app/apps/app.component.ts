import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App, AppOperation } from '../app.entity';
import { AccountService } from '../account/account.service';
import { NewVersionDialog } from './new-version.dialog';
import { HostsService } from '../hosts/hosts.service';
import { AddHostDialog } from './add-host.dialog';
import { Version } from '../app.entity';
import { JobPlayDialog } from './job-play.dialog';


@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    public svnaddr = 'svn://127.0.0.1';
    app: App;
    operations: AppOperation[];
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
                this.svc.getOperationsByAppTypeId(a.appType.id).subscribe(ret => {
                    if (ret.code == 0) {
                        this.operations = ret.result;
                    }
                })
            }
        });

    }

    ngOnInit() {}

    onOperationClick(operation: AppOperation) {
        let ref = this.modal.open(JobPlayDialog, {size: 'lg', scrollable: true});
        ref.componentInstance.operation = operation;
    }
}
