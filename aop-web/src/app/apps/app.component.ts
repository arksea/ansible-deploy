import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { FormGroup, FormControl } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, AppOperation, HostStatus } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Version, Host } from '../app.entity'
import { JobPlayDialog, StatusJobPlayDialog } from './job-play.dialog'

@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
    app: App = new App()
    operations: AppOperation[] = []
    hostChecked: FormGroup = new FormGroup({})
    constructor(private svc: AppsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private modal: NgbModal,
                private alert: MessageNotify) {
        let appId = Number(this.route.snapshot.paramMap.get('id'))
        this.svc.getAppById(appId).subscribe(ret => {
            if (ret.code == 0) {
                this.app = ret.result
                this.initHostCheckedStatus(this.app)
                this.svc.getOperationsByAppTypeId(this.app.appType.id).subscribe(ret => {
                    if (ret.code == 0) {
                        this.operations = ret.result
                    }
                })
            } else {
                this.alert.warning("应用不存在或无权限(id="+appId+")")
                this.router.navigate(["/apps"])
            }
        })
    }

    ngOnInit() {}

    initHostCheckedStatus(app: App) {
        for (let v of app.versions) {
            for (let h of v.targetHosts) {
                let n = this.checkName(v,h)
                this.hostChecked.addControl(n,new FormControl(false))
            }
            let selectAllName = this.selectAllName(v)
            let selectAll = new FormControl(false)
            this.hostChecked.addControl(selectAllName, selectAll)
            selectAll.valueChanges.subscribe(s => {
                for (let h of v.targetHosts) {
                    let c = this.hostChecked.get(this.checkName(v, h))
                    c.setValue(s)
                }
            })
        }
    }

    checkName(v: Version, h: Host): string {
        return v.id+'-'+h.id
    }

    selectAllName(v: Version): string {
        return 'select-all-' + v.id
    }

    onEditBtnClick(app: App) {
        this.router.navigate(['/apps/' + app.id + '/edit'])
    }

    onVersionBtnClick(app: App) {
        this.router.navigate(['/apps/' + app.id + '/versions'])
    }

    hasHostChecked(ver: Version): boolean {
        for (let h of ver.targetHosts) {
            let name = this.checkName(ver, h)
            let c = this.hostChecked.get(name)
            if (c && c.value) {
                return true
            }
        }
        return false
    }

    onOperationClick(ver: Version, op: AppOperation) {
        let ref = this.modal.open(JobPlayDialog, {size: 'xl', scrollable: true, backdrop: 'static', keyboard: false})
        let hosts: Array<Host> = []
        for (let h of ver.targetHosts) {
            let name = this.checkName(ver, h)
            let c = this.hostChecked.get(name)
            if (c && c.value) {
                hosts.push(h)
            }
        }
        ref.componentInstance.setParams(op, this.app, ver, hosts)
    }

    onStatusOperationClick(ver: Version, op: AppOperation) {
        let ref = this.modal.open(StatusJobPlayDialog, {size: 'xl', scrollable: true, backdrop: 'static', keyboard: false})
        ref.componentInstance.setParams(op, this.app, ver, ver.targetHosts)
    }

    public getHostStatusStyle(s: HostStatus): string {
        if (s == undefined) {
            return 'badge-secondary'
        } else {

            switch(s.color) {
                case '0':
                case 'offline':
                return 'badge-danger'
                case '1':
                case 'online':
                    return 'badge-success'
                case '2':
                case 'running':
                    return 'badge-info'
                case '3':
                    return 'badge-warning'
                case '4':
                case 'stopped':
                    return 'badge-secondary'
                case '5':
                    return 'badge-light'
                case '6':
                    return 'badge-dark'
                default:
                    return 'badge-primary'
            }
        } 
    }

    public getHostStatusValue(s: HostStatus): string {
        if (s == undefined) {
            return '未知'
        } else {
            return s.value
        } 
    }

    appHasTargetHosts(app: App): boolean {
        for (let v of app.versions) {
            if (v.targetHosts.length > 0) {
                return true
            }
        }
        return false
    }

    getBuildNoAndDeployNo(v: Version): string {
        if (v.buildNo == 0) {
            return ''
        } else {
            return '(b'+  v.buildNo +'/b' + (v.deployNo?v.deployNo:'') +   ')'
        }

    }

    get status(): AppOperation[] {
        return this.operations.filter(it => it.type=='STATUS')
    }
}
