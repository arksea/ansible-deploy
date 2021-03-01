import { Component } from '@angular/core'
import { FormGroup,FormControl,Validators,ValidatorFn,AbstractControl,ValidationErrors } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { PortsService } from './ports.service'
import { MessageNotify } from '../utils/message-notify'
import { PortSection } from '../app.entity'


@Component({
    selector: 'edit-section-dialog',
    templateUrl: './edit-section.dialog.html'
})
export class EditSectionDialog {
    title: string
    section: PortSection
    form: FormGroup
    isEditAction: boolean

    constructor(public modal: NgbActiveModal, public svc: PortsService, private alert: MessageNotify) {
        this.form = new FormGroup({
            min: new FormControl(8000,[Validators.required,Validators.max(65535),Validators.min(1000)]),
            max: new FormControl(8100,[Validators.required,Validators.max(65535),Validators.min(1000)]),
            type: new FormControl(1, [Validators.required])
        }, { validators: this.portRangeValidator })
    }

    public init(section: PortSection) {
        this.section = section
        this.isEditAction = section.id ? true : false
        this.title = this.isEditAction ? '修改端口区间' : '分配端口区间'
        this.min.setValue(section.minValue)
        this.max.setValue(section.maxValue)
        this.type.setValue(section.type.id)
    }

    portRangeValidator: ValidatorFn = (form: FormGroup): ValidationErrors | null => {
        const min = form.get('min')
        const max = form.get('max')
        const error = Number(max.value) < Number(min.value)
        return error ? { 'portRange': true } : null
    }

    save() {
        let s = new PortSection() //不直接赋值给this.section，是为了防止保存失败，传入对象的值却被修改
        s.id = this.section.id
        s.minValue = this.min.value
        s.maxValue = this.max.value
        let typeId = this.type.value
        s.type = this.svc.portTypesMap[typeId]
        this.svc.savePortSection(s).subscribe(ret => {
            if (ret.code == 0) {
                this.modal.close('ok')
                this.section.type = ret.result.type
                this.section.minValue = s.minValue
                this.section.maxValue = s.maxValue
                this.alert.success('保存成功')
            }
        })
    }

    rangeChanged(): boolean {
        return this.min.value != this.section.minValue || this.max.value != this.section.maxValue
    }

    typeChanged(): boolean {
        return this.type.value != this.section.type.id
    }

    get min() {
        return this.form.get('min')
    }

    get max() {
        return this.form.get('max')
    }

    get type() {
        return this.form.get('type')
    }
}
