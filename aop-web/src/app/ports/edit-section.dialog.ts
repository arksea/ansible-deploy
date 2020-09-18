import { Component } from '@angular/core';
import { FormGroup,FormControl,Validators,ValidatorFn,AbstractControl,ValidationErrors } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PortsService } from './ports.service';
import { MessageNotify } from '../utils/message-notify';
import { PortSection } from '../app.entity';


@Component({
    selector: 'edit-section-dialog',
    templateUrl: './edit-section.dialog.html'
})
export class EditSectionDialog {
    title: string;
    section: PortSection;
    form: FormGroup;
    isEditAction: boolean;
    constructor(public modal: NgbActiveModal, public svc: PortsService, private alert: MessageNotify) {
        this.form = new FormGroup({
            min: new FormControl(8000,[Validators.required,Validators.max(65535),Validators.min(1000)]),
            max: new FormControl(8100,[Validators.required,Validators.max(65535),Validators.min(1000)]),
            type: new FormControl({value:1, disabled:this.isEditAction}, [Validators.required])
        }, { validators: this.portRangeValidator });
    }

    public setSection(section: PortSection) {
        this.section = section;
        this.isEditAction = section.id ? true : false;
        this.title = this.isEditAction ? '修改端口区间' : '分配端口区间';
        this.min.setValue(section.minValue);
        this.max.setValue(section.maxValue);
        this.form.setControl('type', new FormControl({value:section.type.id, disabled:this.isEditAction}, [Validators.required]));
    }

    portRangeValidator: ValidatorFn = (form: FormGroup): ValidationErrors | null => {
        const min = form.get('min');
        const max = form.get('max');
        const error = Number(max.value) < Number(min.value);       
        return error ? { 'portRange': true } : null;
    };

    save() {
        let s = new PortSection();
        s.id = this.section.id;
        s.minValue = this.min.value;
        s.maxValue = this.max.value;
        let typeId = this.type.value;
        s.type = this.svc.portTypesMap[typeId];
        this.svc.savePortSection(s).subscribe(succeed => {
            if (succeed) {
                this.modal.close('ok');
                this.alert.success('保存成功');
            }
        });
    }

    get min() {
        return this.form.get('min');
    }

    get max() {
        return this.form.get('max');
    }

    get type() {
        return this.form.get('type');
    }
}
