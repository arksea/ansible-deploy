import { Component } from '@angular/core';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PortsService } from './ports.service';
import { MessageNotify } from '../utils/message-notify';
import { PortSection, PortType } from '../app.entity';


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
        });
    }

    public setSection(section: PortSection) {
        this.section = section;
        this.isEditAction = section.id ? true : false;
        this.title = this.isEditAction ? '修改端口区间' : '分配端口区间';
        this.min.setValue(section.minValue);
        this.max.setValue(section.maxValue);
        this.type.setValue(section.type.id);
    }

    save() {
        this.section.minValue = this.min.value;
        this.section.maxValue = this.max.value;
        let typeId = this.type.value;
        this.section.type = this.svc.portTypesMap[typeId];
        this.svc.savePortSection(this.section).subscribe(succeed => {
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
