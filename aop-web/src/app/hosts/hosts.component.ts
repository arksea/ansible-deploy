import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { PageEvent } from '@angular/material/paginator';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap'
import { EditHostDialog } from './edit-host.dialog'
import { HostsService } from './hosts.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { Host, HostsPage } from '../app.entity'
import { AccountService } from '../account/account.service'
@Component({
    selector: 'hosts',
    templateUrl: './hosts.component.html'
})
export class HostsComponent implements OnInit {
    pageSize: number = 8
    hostList: HostsPage = new HostsPage()

    constructor(private modal: NgbModal, public svc: HostsService,
            public account: AccountService,
            private alert: MessageNotify) {
        this.query(0, -1, '')
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    })

    ngOnInit(): void {}

    search(event: FormDataEvent) {
        event.preventDefault()
        let pre = this.searchForm.get('searchPrefix').value
        this.query(0, -1, pre)
    }

    public onPageEvent(event: PageEvent): PageEvent {
        let page = event.pageIndex + 1;
        this.pageSize = event.pageSize;
        let search = this.searchForm.get('searchPrefix').value
        this.query(page, -1, search)
        return event;
    }

    query(page:number, groupId: number, ipSearch: string) {
        this.svc.getHosts(page, this.pageSize, groupId, ipSearch).subscribe(ret => {
            if (ret.code == 0) {
                this.hostList = ret.result
            }
        })
    }

    newHost() {
        let ref = this.modal.open(EditHostDialog)
        let host = new Host()
        ref.componentInstance.setHost(host)
        ref.result.then(result => {
            if (result == 'ok') {
                this.hostList.items.push(host)
                this.alert.success('保存成功')
            }
        }, reason => {})
    }

    onDelHostBtnClick(host: Host) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除主机: " + host.privateIp
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteHost(host.id).subscribe(ret => {
                    if (ret.code == 0) {
                        this.hostList.items = this.hostList.items.filter(it => it.id != host.id)
                        this.alert.success('已删除')
                    } else {
                        this.alert.error('无法删除主机，可能有应用部署')
                    }
                })
            }
        }, resaon => { })
    }

    editHost(host: Host) {
        let ref: NgbModalRef = this.modal.open(EditHostDialog)
        ref.componentInstance.setHost(host)
        ref.result.then(result => {
            if (result == 'ok') {
                this.alert.success('保存成功')
            }
        }, reason => {})
    }

    switchStatus(host: Host) {
        let ref = this.modal.open(ConfirmDialog)
        let opType = host.enabled ? "禁用" : "启用"
        ref.componentInstance.title = "确认要" + opType + "吗?"
        ref.componentInstance.message = "此操作将" + opType + "主机: " + host.privateIp
        ref.result.then(result => {
            if (result == "ok") {
                host.enabled = !host.enabled
                this.svc.saveHost(host).subscribe(ret => {
                    if (ret.code == 0) {
                        this.alert.success(opType + '主机成功')
                    }
                })
            }
        }, resaon => {})
    }
}