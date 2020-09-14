import { Component } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App, Version } from '../app.entity';

@Component({
    selector: 'new-version-dialog',
    templateUrl: './new-version.dialog.html'
})
export class NewVersionDialog {
    public appId: number;
    public app: Observable<App>;
    constructor(public modal: NgbActiveModal, public svc: AppsService, private alert: MessageNotify) {
    }

    public createForm: FormGroup = new FormGroup({
        name: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(64)]),
        repository: new FormControl('',[Validators.required,Validators.maxLength(1000)]),
        javaOpt: new FormControl('',[Validators.maxLength(1000)]),
        revision: new FormControl('HEAD',[Validators.required,Validators.minLength(1),Validators.maxLength(64)]),
    });

    create(event: FormDataEvent) {
        event.preventDefault();
        let ver = new Version();
        ver.name = this.createForm.get('name').value;
        ver.repository = this.createForm.get('repository').value;
        ver.javaOpt = this.createForm.get('javaOpt').value;
        ver.revision = this.createForm.get('revision').value;
        this.svc.createVersion(this.appId, ver).subscribe(error => {
            if (!error) {
                this.modal.close('ok');
                this.alert.success('新建版本成功');
            }
        });
    }
}
