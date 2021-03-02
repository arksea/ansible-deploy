import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, Host, AppVariable } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { NewVersionDialog } from './new-version.dialog'
import { AddHostDialog } from './add-host.dialog'
import { Version } from '../app.entity'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { DeleteJobPlayDialog } from './job-play.dialog'
import { PortSelectDialog } from './port-select.dialog'

@Component({
    selector: 'version-list',
    templateUrl: './version-list.component.html'
})
export class VersionListComponent implements OnInit {

    public app: App
    public appForm: FormGroup
    portModifyed: boolean = false

    constructor(private svc: AppsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.app = this.svc.createDefAppTemplate()
        this.appForm = new FormGroup({
            'appGroupId': new FormControl(0, [Validators.required]),
            'apptag': new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(30)])
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
                this.apptag.setValue(this.app.apptag)
                if (this.app.appGroup != null) {
                    this.appGroupId.setValue(this.app.appGroup.id)
                }
            } else {
                this.router.navigate(["/apps"])
            }
        })
    }

    ngOnInit(): void {
    }

    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog)
        this.app.apptag = this.apptag.value
        ref.componentInstance.app = this.app
    }

    onAddHostBtnClick(version: Version) {
        if (this.appGroupId.value) {
            let ref = this.modal.open(AddHostDialog)
            ref.componentInstance.setParams(this.app, version, this.appGroupId.value)
        } else {
            this.alert.warning("应用还未加入分组，不能配置部署主机")
        }
    }

    onEditBtnClick(version: Version) {
        let ref = this.modal.open(NewVersionDialog)
        ref.componentInstance.app = this.app
        ref.componentInstance.version = version
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
 
    public onSelectPortBtnClick(variable: AppVariable) {
        let ref = this.modal.open(PortSelectDialog)
        ref.componentInstance.variable = variable
        ref.result.then(result => {
            if (result == 'ok') {
                this.appForm.get('var_' + variable.name).setValue(variable.value)
                this.portModifyed = true
            }
        }, reason => {})
    }

    getVarDesc(app: App, variable: AppVariable): string {
        let def = this.svc.getAppVarDefine(app.appType.id, variable.name)
        return def ? def.formLabel : ''
    }

    getInputAddon(app: App, variable: AppVariable): string {
        let def = this.svc.getAppVarDefine(app.appType.id, variable.name)
        return def ? def.inputAddon : ''
    }

    getInputType(variable: AppVariable): string {
        switch(variable.name) {
            default:
                return 'text'
        }
    }

    public cancel() {
        this.router.navigate(['/apps'])
    }

    public get apptag(): AbstractControl {
        return this.appForm.get('apptag')
    }

    public get appGroupId(): AbstractControl {
        return this.appForm.get('appGroupId')
    }
}
