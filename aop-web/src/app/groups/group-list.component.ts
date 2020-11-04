import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { Router } from '@angular/router'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { NewGroupDialog } from './new-group.dialog'
import { GroupsService } from './groups.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { AppGroup } from '../app.entity'
import { AccountService } from '../account/account.service'

@Component({
    selector: 'group-list',
    templateUrl: './group-list.component.html'
})
export class GroupListComponent implements OnInit {

    groupList: Array<AppGroup> = new Array()
    constructor(private modal: NgbModal, 
            private svc: GroupsService,
            private account: AccountService,
            private alert: MessageNotify,
            private router: Router) {
        this.svc.getGroups().subscribe(ret => {
            if (ret.code == 0) {
                this.groupList = ret.result
            }
        })
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    })

    ngOnInit(): void {
    }

    search(event: FormDataEvent) {
        event.preventDefault()
        let pre = this.searchForm.get('searchPrefix').value
    }

    newGroup() {
        let ref = this.modal.open(NewGroupDialog)
        let g: AppGroup = new AppGroup()
        ref.componentInstance.appGroup = g
        ref.result.then(result => {
            if (result == 'ok') {
                this.groupList.push(g)
            }
        }, reason => {})
    }

    editGroup(group: AppGroup) {
        let ref = this.modal.open(NewGroupDialog)
        ref.componentInstance.appGroup = group
        ref.result.then(result => {}, reason => {})
    }

    deleteGroup(group: AppGroup) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "确认要删除吗?"
        ref.componentInstance.message = "确认后将删除组: " + group.name
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteGroup(group).subscribe(ret => {
                    if (ret.code == 0) {
                        this.groupList = this.groupList.filter(it => it.id != group.id)
                        this.alert.success('删除组成功')
                    }
                })
            }
        }, resaon => {})
    }

    onViewBtnClick(group: AppGroup) {
        this.svc.setSelectedGroup(group.id)
        this.router.navigate(['/groups', group.id, 'members'])
    }

    perm(): boolean {
        return this.account.perm('组管理:修改')
    }

    canDel(g: AppGroup): boolean {
        return g.apps.length==0 && g.users.length==0 || g.hosts.length==0
    }
}
