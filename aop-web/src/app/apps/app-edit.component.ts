import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App,AppGroup, Host, AppVariable } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { NewVersionDialog } from './new-version.dialog'
import { HostsService } from '../hosts/hosts.service'
import { AddHostDialog } from './add-host.dialog'
import { Version } from '../app.entity'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { JobPlayDialog } from './job-play.dialog'
import { PortSelectDialog } from './port-select.dialog'

@Component({
    selector: 'app-edit',
    templateUrl: './app-edit.component.html'
})
export class AppEditComponent implements OnInit {

    public groupSelectModel: any
    public app: App
    public appForm: FormGroup
    public isNewAction: boolean
    public userGroups: AppGroup[]
    portModifyed: boolean = false

    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.app = this.svc.createDefAppTemplate()
        this.appForm = this.makeFormGroup(this.app)
        this.svc.getUserGroups().subscribe(ret => {
            if (ret.code == 0) {
                this.userGroups = ret.result
                let params: ParamMap =  this.route.snapshot.paramMap
                let idStr = params.get('id')
                let appType = params.get('appType')
                this.queryApp(idStr, appType)
            }
        })
    }

    private queryApp(idStr: string, appType: string) {
        if (idStr == 'new') {
            this.isNewAction = true
            this.svc.createAppTemplate(appType).subscribe(
                ret => {
                    if (ret.code == 0) {
                        this.app = ret.result
                        this.appForm = this.makeFormGroup(this.app)
                    }
                }
            )
        } else {
            this.isNewAction = false
            let appId = Number(idStr)
            this.svc.getAppById(appId).subscribe(ret => {
                if (ret.code == 0) {
                    this.app = ret.result
                    this.appForm = this.makeFormGroup(this.app)
                } else {
                    this.router.navigate(["/apps"])
                }
            })
        }
    }

    ngOnInit(): void {
    }

    public makeFormGroup(app: App): FormGroup {
        let tag = new FormControl({ value: app.apptag, disabled: app.id }, [Validators.required, Validators.minLength(4), Validators.maxLength(30)])
        let appGroupId = new FormControl(app.appGroupId, [Validators.required])
        let f =  new FormGroup({
            'appGroupId': appGroupId,
            'apptag': tag,
            'enableJmx': new FormControl(app.enableJmx),
            'description': new FormControl(app.description, [Validators.required, Validators.minLength(5), Validators.maxLength(256)])
        })
        for (let v of app.vars) {
            f.addControl('var_' + v.name, new FormControl({ value: v.value, disabled: v.isPort }, [Validators.maxLength(128)]))
        }
        return f
    }

    public save() {
        let f = this.appForm
        let a = this.app
        a.apptag = this.apptag.value
        a.description = this.desc.value
        a.appGroupId = Number(this.appGroupId.value)
        a.enableJmx = this.enableJmx.value
        for (let i of a.vars) {
            let c = f.get('var_' + i.name)
            i.value = c.value
        }
        this.svc.saveApp(a).subscribe(ret => {
                if (ret.code == 0) {
                    this.alert.success('保存应用成功')
                    this.router.navigate(['/apps'])
                }
            }
        )
    }


    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog)
        this.app.apptag = this.apptag.value
        ref.componentInstance.app = this.app
    }

    onAddHostBtnClick(version: Version) {
        if (this.appGroupId.value) {
            this.hostSvc.getHostsInGroup(this.appGroupId.value).subscribe(
                ret => {
                    if (ret.code == 0) {
                        let ref = this.modal.open(AddHostDialog)
                        ref.componentInstance.setParams(this.app, version, ret.result)
                    }
                }
            )
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
        if (this.isNewAction) { //未保存的新建应用
            this.app.versions = this.app.versions.filter((v,i,a) => v.name != version.name)
        } else {
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
    }

    onDeleteHostBtnClick(host: Host, version: Version) {
        if (this.isNewAction) { //未保存的新建应用
            version.targetHosts = version.targetHosts.filter((h,i,a) => h.id != host.id)
        } else {
            let ref = this.modal.open(ConfirmDialog)
            ref.componentInstance.title = "从版本中移除主机: "+host.privateIp
            ref.componentInstance.message = "确认要移除吗?"
            ref.result.then(result => {
                if (result == "ok") {
                    this.doDeleteOperation(host, version)
                }
            }, resaon => {})
        }
    }

    private doDeleteOperation(host: Host, ver: Version) {
        this.svc.getOperationsByAppTypeId(this.app.appType.id).subscribe(ret => {
            if (ret.code == 0) {
                let operations = ret.result
                let hasDelScript = false
                for (let op of operations) {
                    if (op.type == 'DELETE') {
                        hasDelScript = true
                        let ref = this.modal.open(JobPlayDialog, {size: 'lg', scrollable: true})
                        ref.componentInstance.operation = op
                        ref.componentInstance.app = this.app
                        ref.componentInstance.hosts = [host]
                        ref.result.then(result => {
                                if (result == "ok") {
                                    this.doDeleteHostFromVersion(host, ver)
                                }
                            }, reason => {
                                if (reason == 'cancel') {
                                    this.alert.warning("已取消")
                                } else {
                                    this.alert.error("删除失败: " + reason)
                                }
                            })
                        break
                    }
                }
                if (!hasDelScript)  {
                    this.doDeleteHostFromVersion(host, ver)
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

    public get deployPath(): AbstractControl {
        return this.appForm.get('deployPath')
    }

    public get desc(): AbstractControl {
        return this.appForm.get('description')
    }

    public get appGroupId(): AbstractControl {
        return this.appForm.get('appGroupId')
    }

    public get enableJmx() : AbstractControl {
        return this.appForm.get('enableJmx')
    }

}
