import { Component, Input } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupService } from './group.service';
import { MessageNotify } from '../utils/message-notify';

@Component({
    selector: 'new-group-dialog',
    templateUrl: './new-group.dialog.html'
})
export class NewGroupDialog {
    //@Input() ok: string = '确认'
    constructor(public modal: NgbActiveModal, private svc: GroupService, private alert: MessageNotify) {
    }

    createForm: FormGroup = new FormGroup({
        name: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(32)]),
        description: new FormControl('',[Validators.maxLength(100)]),
    });

    create(event: FormDataEvent) {
        event.preventDefault();
        let name = this.createForm.get('name').value;
        let desc = this.createForm.get('description').value;
        this.svc.createGroup(name, desc).subscribe(response => {
            if (response.code === 0) {
                this.modal.close('ok');
                this.alert.success('新建组成功');
            } else {
                this.alert.warning(response.error);
            }
        });
    }
}
