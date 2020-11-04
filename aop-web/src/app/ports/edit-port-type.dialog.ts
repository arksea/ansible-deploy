import { Component } from '@angular/core'
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { PortsService } from './ports.service'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from '../account/account.service';
import { PortType } from '../app.entity'


@Component({
    selector: 'edit-port-type-dialog',
    templateUrl: './edit-port-type.dialog.html'
})
export class EditPortTypeDialog {
    modifing: PortType
    form: FormGroup
    public portTypes: Array<PortType> = []
    modified: boolean = false

    constructor(public modal: NgbActiveModal, public svc: PortsService, public account: AccountService, private alert: MessageNotify) {
        this.svc.getPortTypes().subscribe(ret => {
            if (ret.code == 0) {
                this.portTypes = ret.result
            }
        })
        this.form = new FormGroup({
            formNew: new FormGroup({
                newTypeName: new FormControl('', [Validators.required, Validators.max(16)]),
                newTypeDesc: new FormControl('', [Validators.max(64)])
            }),
            formModify: new FormGroup({
                modifyTypeName: new FormControl('', [Validators.required, Validators.max(16)]),
                modifyTypeDesc: new FormControl('', [Validators.max(64)])
            })
        })
    }

    startModify(t: PortType) {
        this.modifing = t
        this.modifyTypeName.setValue(t.name)
        this.modifyTypeDesc.setValue(t.description)
    }

    cancelModify() {
        this.modifing = undefined
        this.modifyTypeName.setValue('')
        this.modifyTypeDesc.setValue('')
    }

    acceptModify() {
        this.modifing.name = this.modifyTypeName.value
        this.modifing.description = this.modifyTypeDesc.value
        this.modified = true
        this.modifing = undefined
    }

    save() {
        this.svc.savePortTypes(this.portTypes).subscribe(ret => {
            if (ret.code == 0) {
                this.svc.portTypes = ret.result
                this.svc.portTypes.forEach(t => this.svc.portTypesMap[t.id]=t)
                this.modal.close('ok')
                this.alert.success('保存成功')
            }
        })
    }

    onDeleteBtnClick(t) {
        this.portTypes = this.portTypes.filter(it => it.id != t.id)
        this.modified = true
    }

    onAddBtnClick() {
        let t = new PortType()
        t.name = this.newTypeName.value
        t.description = this.newTypeDesc.value
        this.portTypes.push(t)
        this.newTypeName.setValue('')
        this.newTypeDesc.setValue('')
        this.modified = true
    }

    get formNew(): AbstractControl {
        return this.form.get('formNew')
    }

    get formModify(): AbstractControl {
        return this.form.get('formModify')
    }

    get modifyTypeName(): AbstractControl {
        return this.formModify.get('modifyTypeName')
    }

    get modifyTypeDesc(): AbstractControl {
        return this.formModify.get('modifyTypeDesc')
    }

    get newTypeName(): AbstractControl {
        return this.formNew.get('newTypeName')
    }

    get newTypeDesc(): AbstractControl {
        return this.formNew.get('newTypeDesc')
    }
}
