import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup,FormControl,Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { MessageNotify } from '../utils/message-notify'
import { GroupsService } from './groups.service'
import { AppGroup } from '../app.entity'

@Component({
    selector: 'new-group-dialog',
    templateUrl: './new-group.dialog.html'
})
export class NewGroupDialog {

    private _appGroup: AppGroup
    form: FormGroup

    constructor(public modal: NgbActiveModal, private svc: GroupsService, private alert: MessageNotify) {
    }

    set appGroup(group: AppGroup) {
        this._appGroup = group
        this.form = new FormGroup({
            name: new FormControl(group.name,[Validators.required, Validators.minLength(2), Validators.maxLength(32)]),
            description: new FormControl(group.description,[Validators.maxLength(100)]),
        })
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        let name = this.form.get('name').value
        let desc = this.form.get('description').value
        if (this._appGroup.id == undefined) {
            this.svc.createGroup(name, desc).subscribe(ret => {
                if (ret.code == 0) {
                    this._appGroup.id = ret.result
                    this._appGroup.name = name
                    this._appGroup.description = desc
                    this.modal.close('ok')
                    this.alert.success('保存成功')
                }
            })
        } else {
            this.svc.modifyGroup(this._appGroup.id, name, desc).subscribe(ret => {
                if (ret.code == 0) {
                    this._appGroup.name = name
                    this._appGroup.description = desc
                    this.modal.close('ok')
                    this.alert.success('保存成功')
                }
            })
        }
    }
}
