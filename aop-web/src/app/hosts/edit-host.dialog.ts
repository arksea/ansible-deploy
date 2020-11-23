import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup,FormControl,Validators, AbstractControl } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { AccountService } from '../account/account.service'
import { HostsService } from './hosts.service'
import { Host,AppGroup } from '../app.entity'

@Component({
    selector: 'edit-host-dialog',
    templateUrl: './edit-host.dialog.html'
})
export class EditHostDialog {
    title: string
    host: Host
    form: FormGroup
    groups: AppGroup[]
    editing: boolean = false
    constructor(public modal: NgbActiveModal, private svc: HostsService, public account: AccountService) {
        this.form = new FormGroup({
            description: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(32)]),
            privateIp: new FormControl('',[Validators.required, Validators.minLength(7), Validators.maxLength(36)]),
            appGroupId: new FormControl(0, [Validators.required])
        })
        this.svc.getGroups().subscribe(ret => {
            if (ret.code == 0) {
                this.groups = ret.result
            }
        })
    }

    public setHost(host: Host) {
        this.host = host
        this.editing = host.id ? true : false
        this.title = this.editing ? '修改主机信息' : '新增主机'
        this.desc.setValue(host.description)
        this.ip.setValue(host.privateIp)
        this.gid.setValue(host.appGroupId)
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        this.host.description = this.desc.value
        this.host.privateIp = this.ip.value
        this.host.appGroupId = this.gid.value
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

    get desc(): AbstractControl {
        return this.form.get('description')
    }

    get ip(): AbstractControl {
        return this.form.get('privateIp')
    }

    get gid(): AbstractControl {
        return this.form.get('appGroupId')
    }

}
