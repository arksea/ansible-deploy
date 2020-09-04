import { Component } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';

@Component({
    selector: 'new-group-dialog',
    templateUrl: './new-group.dialog.html'
})
export class NewGroupDialog {

    constructor(public modal: NgbActiveModal, private svc: GroupsService, private alert: MessageNotify) {
    }

    createForm: FormGroup = new FormGroup({
        name: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(32)]),
        description: new FormControl('',[Validators.maxLength(100)]),
    });

    create(event: FormDataEvent) {
        event.preventDefault();
        let name = this.createForm.get('name').value;
        let desc = this.createForm.get('description').value;
        this.svc.createGroup(name, desc).subscribe(error => {
            if (!error) {
                this.modal.close('ok');
                this.alert.success('新建组成功');
            }
        });
    }
}
