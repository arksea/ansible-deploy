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
    constructor(private modal: NgbModal, public svc: GroupsService,
            public account: AccountService,
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
        ref.result.then(result => {
            if (result instanceof AppGroup) {
                this.groupList.push(result)
                this.alert.success('新建组成功')
            }
        }, reason => {})
    }

    deleteGroup(group: AppGroup) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除组: " + group.name
        ref.componentInstance.message = "确认要删除吗?"
        ref.componentInstance.detail = "此操作将把组'" + group.name + "'标记为不可用状态，不会直接删除组及其相关资源数据"
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
}
