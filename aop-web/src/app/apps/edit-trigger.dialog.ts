import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup,FormControl,Validators, AbstractControl } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { AccountService } from '../account/account.service'
import { TriggerService } from './trigger.service'
import { OperationTrigger, AppOperation, AppType } from '../app.entity'
import { now } from 'moment'
import { AppsService } from './apps.service'

@Component({
    selector: 'edit-trigger-dialog',
    templateUrl: './edit-trigger.dialog.html'
})
export class EditTriggerDialog {
    title: string
    triggers: OperationTrigger[]
    trigger: OperationTrigger
    appTypeId: number
    form: FormGroup
    operations: AppOperation[]
    editing: boolean = false
    private operationsMap: Map<number,AppOperation> = new Map()
    constructor(public modal: NgbActiveModal, protected svc: TriggerService, protected appSvc: AppsService, public account: AccountService) {
    }

    public setParams(appTypeId: number, trigger: OperationTrigger, triggers: OperationTrigger[]) {
        this.appTypeId = appTypeId
        this.trigger = trigger
        this.triggers = triggers
        this.editing = trigger.id ? true : false
        this.title = this.editing ? '修改触发器信息' : '新增触发器'
        let days =  trigger.expiredTime == 0 ? 10000 : (trigger.expiredTime - now())/1000/3600/24
        this.form = new FormGroup({
            operationId: new FormControl({value:trigger.operationId, disabled: this.editing}, [Validators.required]),
            description: new FormControl(trigger.description,[Validators.maxLength(128)]),
            expireDays: new FormControl(days.toFixed(), [Validators.required]),
            projectTag: new FormControl(trigger.projectTag, [Validators.required, Validators.maxLength(128)]),
            notifyOnlyOfFailed: new FormControl(trigger.notifyOnlyOfFailed),
            notifyEmails: new FormControl(trigger.notifyEmails, [Validators.maxLength(1024)])
        })
        this.appSvc.getOperationsByAppTypeId(this.appTypeId).subscribe(ret => {
            if (ret.code == 0) {
                this.operations = ret.result
                this.operations.forEach(it => this.operationsMap.set(it.id, it))
            }
        })
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        this.trigger.description = this.desc.value
        this.trigger.expiredTime = now() + this.expireDays.value*24*3600*1000;
        this.trigger.operationId = this.operationId.value
        this.trigger.projectTag = this.projectTag.value
        this.trigger.notifyOnlyOfFailed = this.notifyOfFailed.value
        this.trigger.notifyEmails = this.emails.value
        this.svc.saveTrigger(this.trigger).subscribe(ret => {
            if (ret.code == 0) {
                if (!this.editing) {
                    this.triggers.push(ret.result)
                }
                this.modal.close('ok')
            }
        })
    }

    get desc(): AbstractControl {
        return this.form.get('description')
    }

    get operationId(): AbstractControl {
        return this.form.get('operationId')
    }

    get expireDays(): AbstractControl {
        return this.form.get('expireDays')
    }

    get projectTag(): AbstractControl {
        return this.form.get('projectTag')
    }

    get emails(): AbstractControl {
        return this.form.get('notifyEmails')
    }

    get notifyOfFailed(): AbstractControl {
        return this.form.get('notifyOnlyOfFailed')
    }
}
