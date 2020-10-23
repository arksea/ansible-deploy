import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { Observable } from 'rxjs'
import { AppTypesService } from './app-types.service'
import { MessageNotify } from '../utils/message-notify'
import { AppType, AppVarDefine } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { HostsService } from '../hosts/hosts.service'
import { ConfirmDialog } from '../utils/confirm.dialog'


@Component({
    selector: 'app-type-edit',
    templateUrl: './app-type-edit.component.html'
})
export class AppTypeEditComponent implements OnInit {

    public isNewAction: boolean
    public appType: AppType
    public typeForm: FormGroup = new FormGroup({
        name: new FormControl('', [Validators.required, Validators.maxLength(32)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(128)])
    })

    constructor(private svc: AppTypesService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
        let params: ParamMap =  this.route.snapshot.paramMap
        let idStr = params.get('id')
        if (idStr == 'new') {
            this.isNewAction = true
            this.appType = new AppType()
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

    }

    get name() {
        return this.typeForm.get('name')
    }

    get description() {
        return this.typeForm.get('description')
    }

}
