import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { MessageNotify } from '../utils/message-notify'
import { PageEvent } from '@angular/material/paginator'
import { AppsService } from './apps.service'
import { Host, Version, App, Page } from '../app.entity'
import { FormGroup, FormArray, FormControl, Validators } from '@angular/forms'
import { HostsService } from '../hosts/hosts.service'

@Component({
    selector: 'add-host-dialog',
    templateUrl: './add-host.dialog.html'
})
export class AddHostDialog {
    public pageSize: number = 10
    app: App
    version: Version
    appGroupId: number
    hosts: Page<Host> = new Page()
    public formArray: FormArray = new FormArray([])
    public formGroup: FormGroup = new FormGroup({hosts: this.formArray})

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    })

    constructor(public modal: NgbActiveModal, public svc: AppsService, private hostSvc: HostsService, private alert: MessageNotify) {
    }

    search(event: FormDataEvent) {
        event.preventDefault()
        let pre = this.searchForm.get('searchPrefix').value
        this.query(0, pre)
    }

    public onPageEvent(event: PageEvent): PageEvent {
        let page = event.pageIndex + 1
        this.pageSize = event.pageSize
        let search = this.searchForm.get('searchPrefix').value
        this.query(page, search)
        return event
    }

    query(page:number, search: string) {
        this.hostSvc.getHostsInGroup(page, this.pageSize, search, this.appGroupId).subscribe(
            ret => {
                if (ret.code == 0) {
                    this.hosts = ret.result
                    this.hosts.items = this.hosts.items.filter((h,i,array)=> {
                        for (let v of this.app.versions) {
                            if (v.targetHosts) {
                                for (let t of v.targetHosts) { //过滤应用在所有版本中已添加的主机
                                    if (t.id == h.id) {
                                        return false
                                    }
                                }
                            }
                        }
                        return true
                    })
                    this.formArray.clear()
                    this.hosts.items.forEach(h => this.formArray.push(new FormControl({value:false,disabled:!h.enabled})))
                }
            }
        )
    }

    public setParams(app: App, version: Version, appGroupId: number) {
        this.version = version
        this.app = app
        this.appGroupId = appGroupId
        this.query(0,'')
    }

    save() {
        let idList: number[] = []
        let addHosts: Host[] = []
        for (let i in this.hosts.items) {
            if (this.formArray.value[i]) {
                let h = this.hosts.items[i]
                idList.push(h.id)
                addHosts.push(h)
            }
        }
        if (this.app.id) {
            this.svc.addHostsToVersion(this.version.id, idList).subscribe(error => {
                if (!error) {
                    addHosts.forEach(h => this.version.targetHosts.push(h))
                    this.modal.close('ok')
                    this.alert.info("新增主机成功")
                }
            })
        } else { //未保存的新建应用
            addHosts.forEach(h => this.version.targetHosts.push(h))
            this.modal.close('ok')
        }
    }
}
