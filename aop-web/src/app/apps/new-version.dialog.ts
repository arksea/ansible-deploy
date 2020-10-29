import { Component } from '@angular/core';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App, Version } from '../app.entity';

@Component({
    selector: 'new-version-dialog',
    templateUrl: './new-version.dialog.html'
})
export class NewVersionDialog {
    app: App;
    private _version: Version;
    private isNewAction: boolean;
    constructor(public modal: NgbActiveModal, public svc: AppsService, private alert: MessageNotify) {
        this.version = new Version();
        this.isNewAction = true;
    }

    public form: FormGroup;

    set version(ver: Version) {
        this._version = ver;
        this.isNewAction = false;
        this.form = new FormGroup({
            name: new FormControl(ver.name,[Validators.required, Validators.minLength(2), Validators.maxLength(64)]),
            repository: new FormControl(ver.repository,[Validators.required,Validators.maxLength(1000)]),
            execOpt: new FormControl(ver.execOpt,[Validators.maxLength(1000)]),
            revision: new FormControl(ver.revision,[Validators.required,Validators.minLength(1),Validators.maxLength(64)]),
        });
    }

    save() {
        event.preventDefault();
        let ver = this._version;
        ver.name = this.form.get('name').value;
        ver.repository = this.form.get('repository').value;
        ver.execOpt = this.form.get('execOpt').value;
        ver.revision = this.form.get('revision').value;
        if (this.app.id) {
            if (!ver.appId) {
                ver.appId = this.app.id;
            }
            this.svc.saveVersion(ver).subscribe(id => {
                if (id) {
                    if (this.isNewAction) {
                        ver.id = id;
                        this.app.versions.push(ver);
                    }
                    this.modal.close('ok');
                    this.alert.success('保存成功');
                }
            });
        } else { //未保存的新建应用
            if (this.isNewAction) {
                this.app.versions.push(ver);
            }
            this.modal.close('ok');
        }

    }
}
