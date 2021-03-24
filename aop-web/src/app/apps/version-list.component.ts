import { Component, OnInit } from '@angular/core'
import { FormGroup } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, Host, AppVariable, OperationTrigger } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { AddHostDialog } from './add-host.dialog'
import { EditTriggerDialog } from './edit-trigger.dialog'
import { Version } from '../app.entity'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { DeleteJobPlayDialog } from './job-play.dialog'
import { TriggerService } from './trigger.service'

@Component({
    selector: 'version-list',
    templateUrl: './version-list.component.html',
    styles: ['.table-header { background-color: rgba(0, 0, 0, 0.03); }']
})
export class VersionListComponent implements OnInit {

    public app: App
    public appForm: FormGroup
    portModifyed: boolean = false

    constructor(private svc: AppsService,
                public account: AccountService,
                private triggerSvc: TriggerService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.app = this.svc.createDefAppTemplate()
        this.appForm = new FormGroup({
        })
        let params: ParamMap =  this.route.snapshot.paramMap
        let idStr = params.get('id')
        this.queryApp(idStr)
    }

    private queryApp(idStr: string) {
        let appId = Number(idStr)
        this.svc.getAppById(appId).subscribe(ret => {
            if (ret.code == 0) {
                this.app = ret.result
            } else {
                this.router.navigate(["/apps"])
            }
        })
    }

    ngOnInit(): void {
    }

    onNewVersionBtnClick() {
        this.router.navigate(['/apps',this.app.id,'versions', 'new', 'edit', this.app.appType.name])
    }

    onAddHostBtnClick(version: Version) {
        if (this.app.appGroup.id) {
            let ref = this.modal.open(AddHostDialog)
            ref.componentInstance.setParams(this.app, version, this.app.appGroup.id)
        } else {
            this.alert.warning("应用还未加入分组，不能配置部署主机")
        }
    }

    onAddTriggerBtnClick(version: Version) {
        let ref = this.modal.open(EditTriggerDialog)
        let trigger = new OperationTrigger()
        trigger.versionId = version.id
        trigger.createUser = this.account.loginUser
        trigger.createTime = new Date().getTime()
        ref.componentInstance.setParams(this.app.appType.id, trigger, version.triggers)
    }

    onEditBtnClick(version: Version) {
        this.router.navigate(['/apps',this.app.id,'versions', version.id, 'edit'])
    }

    onDeleteVersionBtnClick(version: Version) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除版本: "+version.name
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteVersionById(version.id).subscribe(success => {
                    if (success) {
                        this.app.versions = this.app.versions.filter((v,i,a) => v.id != version.id)
                        this.alert.success("已删除")
                    }
                })
            }
        }, resaon => {})
    }

    private confirmDeleteHost(host: Host, version: Version) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "确认要移除吗?"
        ref.componentInstance.message = "从版本中移除主机: "+host.privateIp
        ref.result.then(result => {
            if (result == "ok") {
                this.doDeleteHostFromVersion(host, version)
            }
        }, resaon => {})
    }

    onDeleteHostBtnClick(host: Host, ver: Version) {
        this.svc.getOperationsByAppTypeId(this.app.appType.id).subscribe(ret => {
            if (ret.code == 0) {
                let operations = ret.result
                let hasDelScript = false
                for (let op of operations) {
                    if (op.type == 'DELETE') {
                        hasDelScript = true
                        let ref = this.modal.open(DeleteJobPlayDialog, {size: 'lg', scrollable: true})
                        ref.componentInstance.operation = op
                        ref.componentInstance.app = this.app
                        ref.componentInstance.hosts = [host]
                        ref.componentInstance.ver = ver
                        ref.result.then(result => {
                                if (result == 'skip') {
                                    this.confirmDeleteHost(host, ver)
                                } else if (result == "ok") {
                                    this.doDeleteHostFromVersion(host, ver)
                                }
                            }, reason => {
                                this.alert.error("删除失败: " + reason)
                            })
                        break
                    }
                }
                if (!hasDelScript)  {
                    this.confirmDeleteHost(host, ver)
                }
            }
        })
    }

    private doDeleteHostFromVersion (host: Host, version: Version) {
        this.svc.removeHostFromVersion(version.id, host.id).subscribe(success => {
            if (success) {
                version.targetHosts = version.targetHosts.filter((h,i,a) => h.id != host.id)
                this.alert.success("成功移除")
            }
        })
    }

    public onDeleteTriggerBtnClick(trigger: OperationTrigger, version: Version) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "确认要移除吗?"
        ref.componentInstance.message = "从版本中移除触发器: "+trigger.id
        ref.result.then(result => {
            if (result == "ok") {
                this.triggerSvc.deleteTrigger(trigger.id).subscribe(success => {
                    if (success) {
                        version.triggers = version.triggers.filter((t,i,a) => t.id != trigger.id)
                        this.alert.success("成功移除")
                    }
                })
            }
        }, resaon => {})
    }

    public onEditTriggerBtnClick(trigger: OperationTrigger, version: Version) {
        let ref = this.modal.open(EditTriggerDialog)
        ref.componentInstance.setParams(this.app.appType.id, trigger, version.triggers)
    }

    getVarDesc(app: App, variable: AppVariable): string {
        let def = this.svc.getVersionVarDefine(app.appType.id, variable.name)
        return def ? def.formLabel : ''
    }
}
