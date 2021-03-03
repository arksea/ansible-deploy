import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, VersionVariable, Version, AppInfo } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { HostsService } from '../hosts/hosts.service'
import { PortSelectDialog } from './port-select.dialog'

@Component({
    selector: 'version-edit',
    templateUrl: './version-edit.component.html'
})
export class VersionEditComponent implements OnInit {

    public appId: number
    public appInfo: AppInfo
    public version: Version
    public verForm: FormGroup
    public isNewAction: boolean
    portModifyed: boolean = false

    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.version = new Version()
        this.appInfo = new AppInfo()
        this.verForm = new FormGroup({
            name: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(64)]),
            repository: new FormControl('',[Validators.required,Validators.maxLength(1000)]),
            execOpt: new FormControl('',[Validators.maxLength(1000)]),
            revision: new FormControl('',[Validators.required,Validators.minLength(1),Validators.maxLength(64)]),
        })

        let params: ParamMap =  this.route.snapshot.paramMap
        let appIdStr = params.get('id')
        this.appId = Number(appIdStr)
        let verIdStr = params.get('versionId')
        let appType = params.get('appType')
        this.queryVersion(verIdStr, appType)
    }

    private queryVersion(verIdStr: string, appType: string) {
        this.svc.getAppInfoById(this.appId).subscribe(ret => {
            if (ret.code == 0) {
                this.appInfo = ret.result
            } else {
                this.router.navigate(["/apps", this.appId, "versions"])
            }
        })
        if (verIdStr == 'new') {
            this.isNewAction = true
            this.isNewAction = true
            this.svc.createVersionTemplate(appType).subscribe(
                ret => {
                    if (ret.code == 0) {
                        this.version = ret.result
                        this.version.appId = this.appId
                        this.setFormValue(this.version)
                    } else {
                        this.router.navigate(["/apps", this.appId, "versions"])
                    }
                }
            )

        } else {
            this.isNewAction = false
            let verId = Number(verIdStr)
            this.svc.getVersionById(verId).subscribe(ret => {
                if (ret.code == 0) {
                    this.version = ret.result
                    this.setFormValue(this.version)
                } else {
                    this.router.navigate(["/apps", this.appId, "versions"])
                }
            })
        }
    }

    ngOnInit(): void {
    }

    private setFormValue(version: Version) {
        this.name.setValue(version.name)
        this.repository.setValue(version.repository)
        this.revision.setValue(version.revision)
        this.execOpt.setValue(version.execOpt)
        for (let v of version.vars) {
            this.verForm.addControl('var_' + v.name, new FormControl({ value: v.value, disabled: v.isPort }, [Validators.maxLength(128)]))
        }
    }

    public save() {
        let f = this.verForm
        let v = this.version
        v.execOpt = this.execOpt.value
        v.name = this.name.value
        v.repository = this.repository.value
        v.revision = this.revision.value
        for (let i of v.vars) {
            let c = f.get('var_' + i.name)
            i.value = c.value
        }
        this.svc.saveVersion(v).subscribe(id => {
                if (id) {
                    this.alert.success('保存应用版本成功')
                    this.router.navigate(['/apps', v.appId, "versions"])
                }
            }
        )
    }

    public onSelectPortBtnClick(variable: VersionVariable) {
        let ref = this.modal.open(PortSelectDialog)
        ref.componentInstance.variable = variable
        ref.result.then(result => {
            if (result == 'ok') {
                this.verForm.get('var_' + variable.name).setValue(variable.value)
                this.portModifyed = true
            }
        }, reason => {})
    }

    getVarDesc(appTypeId: number, variable: VersionVariable): string {
        let def = this.svc.getVersionVarDefine(appTypeId, variable.name)
        return def ? def.formLabel : ''
    }

    getInputAddon(appTypeId: number, variable: VersionVariable): string {
        let def = this.svc.getVersionVarDefine(appTypeId, variable.name)
        return def ? def.inputAddon : ''
    }

    getInputType(variable: VersionVariable): string {
        switch(variable.name) {
            default:
                return 'text'
        }
    }

    public cancel() {
        this.router.navigate(['/apps', this.appId, "versions"])
    }

    public get name(): AbstractControl {
        return this.verForm.get('name')
    }

    public get repository(): AbstractControl {
        return this.verForm.get('repository')
    }

    public get execOpt(): AbstractControl {
        return this.verForm.get('execOpt')
    }

    public get revision(): AbstractControl {
        return this.verForm.get('revision')
    }
}
