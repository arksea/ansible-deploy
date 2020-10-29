import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup,FormControl,Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { HostsService } from './hosts.service'
import { MessageNotify } from '../utils/message-notify'
import { Host } from '../app.entity'

@Component({
    selector: 'edit-host-dialog',
    templateUrl: './edit-host.dialog.html'
})
export class EditHostDialog {
    title: string
    host: Host
    form: FormGroup
    editing: boolean = false
    constructor(public modal: NgbActiveModal, private svc: HostsService, private alert: MessageNotify) {
        this.form = new FormGroup({
            description: new FormControl(),
            privateIp: new FormControl()
        })
    }

    public setHost(host: Host) {
        this.host = host
        this.editing = host.id ? true : false
        this.title = this.editing ? '修改主机信息' : '新增主机'
        let description = new FormControl(host.description,[Validators.required, Validators.minLength(2), Validators.maxLength(32)])
        let privateIp = new FormControl({value: host.privateIp, disabled: this.editing},
            [Validators.required, Validators.minLength(7), Validators.maxLength(36)])
        this.form = new FormGroup({
            description: description,
            privateIp: privateIp
        })
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        this.host.description = this.form.get('description').value
        this.host.privateIp = this.form.get('privateIp').value
        this.svc.saveHost(this.host).subscribe(ret => {
            if (ret.code == 0) {
                if (!this.editing) {
                    this.host.id = ret.result
                    this.host.createTime = new Date().toISOString()
                }
                this.modal.close('ok')
            }
        })
    }


}
