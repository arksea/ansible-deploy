import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppTypesService } from './app-types.service'
import { MessageNotify } from '../utils/message-notify'
import { AppType, AppVarDefine } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { NewAppVarDefineDialog } from './new-var-define.dialog'


@Component({
    selector: 'app-type-edit',
    templateUrl: './app-type-edit.component.html'
})
export class AppTypeEditComponent implements OnInit {

    public isNewAction: boolean
    public appType: AppType
    public defineModified: boolean = false
    public typeForm: FormGroup = new FormGroup({
        name: new FormControl('', [Validators.required, Validators.maxLength(32)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(128)]),
    })

    constructor(private svc: AppTypesService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        this.appType = new AppType()
        let params: ParamMap =  this.route.snapshot.paramMap
        let idStr = params.get('id')
        if (idStr == 'new') {
            this.isNewAction = true
        } else {
            this.isNewAction = false
            let appTypeId = Number(idStr)
            this.svc.getAppType(appTypeId).subscribe(ret => {
                if (ret.code == 0) {
                    this.appType = ret.result
                    this.name.setValue(this.appType.name)
                    this.description.setValue(this.appType.description)
                } else {
                    this.alert.warning("应用类型不存在或无权限(id="+appTypeId+")")
                    this.router.navigate(["/app-types"])
                }
            });
        }
    }

    ngOnInit(): void {
    }

    save() {
        this.appType.name = this.name.value
        this.appType.description = this.description.value
        this.svc.saveAppType(this.appType).subscribe(ret => {
            if (ret.code == 0) {
                this.alert.success('保存成功')
                this.router.navigateByUrl('/app-types')
            }
        })
    }

    newVarDefine() {
        let ref = this.modal.open(NewAppVarDefineDialog)
        let def = new AppVarDefine()
        def.appTypeId = this.appType.id
        ref.componentInstance.appVarDefine = def
        ref.result.then(ret => {
            if (ret == 'ok') {
                this.appType.appVarDefines.push(def)
                this.defineModified = true
            }
        }, reason => {})
    }

    editVarDefine(def: AppVarDefine) {
        let ref = this.modal.open(NewAppVarDefineDialog)
        ref.componentInstance.appVarDefine = def
        ref.result.then(ret => {
            if (ret == 'ok') {
                this.defineModified = true
            }
        }, reason => {})
    }

    delVarDefine(def: AppVarDefine) {
        let list = []
        for (let d of this.appType.appVarDefines) {
            if (d.name != def.name) {
                list.push(d)
            }
        }
        this.appType.appVarDefines = list
        this.defineModified = true
    }

    get name() {
        return this.typeForm.get('name')
    }

    get description() {
        return this.typeForm.get('description')
    }
}
