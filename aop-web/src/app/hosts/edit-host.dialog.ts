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
    hosts: Host[]
    host: Host
    form: FormGroup
    groups: AppGroup[]
    editing: boolean = false
    private groupMap: Map<number,AppGroup> = new Map()
    constructor(public modal: NgbActiveModal, private svc: HostsService, public account: AccountService) {
        this.form = new FormGroup({
            description: new FormControl('',[Validators.required, Validators.minLength(2), Validators.maxLength(32)]),
            privateIp: new FormControl('',[Validators.required, Validators.minLength(7), Validators.maxLength(36)]),
            appGroupId: new FormControl(0, [Validators.required])
        })
        this.svc.getGroups().subscribe(ret => {
            if (ret.code == 0) {
                this.groups = ret.result
                this.groups.forEach(it => this.groupMap.set(it.id, it))
            }
        })
    }

    public setHost(host: Host, hosts: Host[]) {
        this.host = host
        this.hosts = hosts
        this.editing = host.id ? true : false
        this.title = this.editing ? '修改主机信息' : '新增主机'
        this.desc.setValue(host.description)
        this.ip.setValue(host.privateIp)
        if (host.appGroup != null) {
            this.gid.setValue(host.appGroup.id)
        }
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        let str: string = this.ip.value
        if (!this.editing && str.includes('~')) {
            this.svc.batchAddHosts(str, this.desc.value, Number(this.gid.value)).subscribe(ret => {
                if (ret.code == 0) {
                    ret.result.forEach(it => this.hosts.push(it))
                    this.modal.close('ok')
                }
            })
        } else {
            this.host.description = this.desc.value
            this.host.privateIp = this.ip.value
            this.host.enabled = true
            this.host.appGroup = this.groupMap.get(Number(this.gid.value))
            this.svc.saveHost(this.host).subscribe(ret => {
                if (ret.code == 0) {
                    if (!this.editing) {
                        this.hosts.push(ret.result)
                    }
                    this.modal.close('ok')
                }
            })
        }
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
