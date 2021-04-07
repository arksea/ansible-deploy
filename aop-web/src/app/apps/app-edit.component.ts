import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, AbstractControl, Validators, ValidatorFn } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App,AppGroup, AppVariable } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { HostsService } from '../hosts/hosts.service'
import { PortSelectDialog } from './port-select.dialog'


export function forbiddenNameValidator(nameRe: RegExp): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
        const forbidden = !nameRe.test(control.value)
        return forbidden ? {forbiddenName: {value: control.value}} : null
    };
}

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
    private groupMap: Map<number,AppGroup> = new Map()
    portModifyed: boolean = false

    constructor(private svc: AppsService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.app = this.svc.createDefAppTemplate()
        this.appForm = new FormGroup({
            'appGroupId': new FormControl(0, [Validators.required]),
            'apptag': new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(30),
                forbiddenNameValidator(/^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$/)]),
            'description': new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(256)])
        })
        this.svc.getUserGroups().subscribe(ret => {
            if (ret.code == 0) {
                this.userGroups = ret.result
                this.userGroups.forEach(it => this.groupMap.set(it.id, it))
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
                        this.setFormValue(this.app)
                    } else {
                        this.router.navigate(["/apps"])
                    }
                }
            )
        } else {
            this.isNewAction = false
            let appId = Number(idStr)
            this.svc.getAppById(appId).subscribe(ret => {
                if (ret.code == 0) {
                    this.app = ret.result
                    this.setFormValue(this.app)
                } else {
                    this.router.navigate(["/apps"])
                }
            })
        }
    }

    ngOnInit(): void {
    }

    private setFormValue(app: App) {
        this.apptag.setValue(app.apptag)
        if (app.appGroup != null) {
            this.appGroupId.setValue(app.appGroup.id)
        }
        this.desc.setValue(app.description)
        for (let v of app.vars) {
            this.appForm.addControl('var_' + v.name, new FormControl({ value: v.value, disabled: v.isPort }, [Validators.maxLength(128)]))
        }
    }

    public save() {
        let f = this.appForm
        let a = this.app
        a.apptag = this.apptag.value
        a.description = this.desc.value
        a.appGroup = this.groupMap.get(Number(this.appGroupId.value))
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
        if (this.isNewAction) {
            this.router.navigate(['/apps'])
        } else {
            this.router.navigate(['/apps', this.app.id])
        }
    }

    public get apptag(): AbstractControl {
        return this.appForm.get('apptag')
    }

    public get desc(): AbstractControl {
        return this.appForm.get('description')
    }

    public get appGroupId(): AbstractControl {
        return this.appForm.get('appGroupId')
    }

    public get appVars(): AppVariable[] {
        let vars = []
        for (let v of this.app.vars) {
            if (!v.deleted) {
                vars.push(v)
            }
        }
        return vars
    }
}
