import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App, AppOperation, HostStatus } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Version, Host } from '../app.entity';
import { JobPlayDialog } from './job-play.dialog';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
    app: App;
    operations: AppOperation[];
    hostChecked: FormGroup = new FormGroup({});
    constructor(private svc: AppsService,
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
                this.initHostCheckedStatus(a);
                this.svc.getOperationsByAppTypeId(a.appType.id).subscribe(ret => {
                    if (ret.code == 0) {
                        this.operations = ret.result;
                    }
                })
            }
        });

    }

    ngOnInit() {}

    initHostCheckedStatus(app: App) {
        for (let v of app.versions) {
            for (let h of v.targetHosts) {
                let n = this.checkName(v,h);
                this.hostChecked.addControl(n,new FormControl(false));
                ///@todo delete test
                //h.status['@@preStatusTest'] = {value: '离线', color: 0} 
            }
            let selectAllName = this.selectAllName(v);
            let selectAll = new FormControl(false);
            this.hostChecked.addControl(selectAllName, selectAll);
            selectAll.valueChanges.subscribe(s => {
                for (let h of v.targetHosts) {
                    let c = this.hostChecked.get(this.checkName(v, h));
                    c.setValue(s);
                }
            })
        }
    }

    checkName(v: Version, h: Host): string {
        return v.id+'-'+h.id;
    }

    selectAllName(v: Version): string {
        return 'select-all-' + v.id
    }

    hasHostChecked(ver: Version): boolean {
        for (let h of ver.targetHosts) {
            let name = this.checkName(ver, h);
            let c = this.hostChecked.get(name);
            if (c && c.value) {
                return true;
            }
        }
        return false;
    }

    onOperationClick(ver: Version, op: AppOperation) {
        let ref = this.modal.open(JobPlayDialog, {size: 'lg', scrollable: true});
        ref.componentInstance.operation = op;
        ref.componentInstance.app = this.app;
        let hosts: Array<Host> = [];
        for (let h of ver.targetHosts) {
            let name = this.checkName(ver, h);
            let c = this.hostChecked.get(name);
            if (c && c.value) {
                hosts.push(h);
            }
        }
        ref.componentInstance.hosts = hosts;
    }

    public getHostStatusStyle(s: HostStatus): string {
        if (s == undefined) {
            return 'badge-secondary';
        } else {

            switch(s.color) {
                case 0:
                    return 'badge-danger';
                case 1:
                    return 'badge-success';
                case 2:
                    return 'badge-info';
                case 3:
                    return 'badge-warning';
                case 4:
                    return 'badge-secondary';
                case 5:
                    return 'badge-light';
                case 6:
                    return 'badge-dark';
                default:
                    return 'badge-primary';
            }
        } 
    }

    public getHostStatusValue(s: HostStatus): string {
        if (s == undefined) {
            return '未知';
        } else {
            return s.value;
        } 
    }
}
