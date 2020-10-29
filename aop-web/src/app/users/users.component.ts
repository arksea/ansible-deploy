import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { UsersService } from './users.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { User } from './users.entity'
import { AccountService } from '../account/account.service'
import { UserRolesDialog } from './user-roles.dialog'

export class UsersComponent {

    userList: Array<User> = []

    constructor(
        public svc: UsersService,
        protected alert: MessageNotify,
        protected modal: NgbModal) {
    }

    protected getUsers(type: string) {
        this.svc.getUsers(type).subscribe(ret => {
            if (ret.code == 0) {
                this.userList = ret.result
            }
        })
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    })

    search(event: FormDataEvent) {
        event.preventDefault()
        //let pre = this.searchForm.get('searchPrefix').value
    }

    newUser() {
        //this.modal.open(NewUserDialog)
    }

    onUserRolesBtnClick(user: User) {
        let ref = this.modal.open(UserRolesDialog, {size: 'lg'})
        ref.componentInstance.user = user
        ref.result.then(result => {
            if (result == 'ok') {
                this.alert.success('修改用户角色成功')
            }
        })
    }
}

@Component({
    selector: 'active-users',
    templateUrl: './users.component.html'
})
export class ActiveUsersComponent extends UsersComponent implements OnInit {

    constructor(
        public svc: UsersService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal) {
        super(svc, alert, modal)
    }

    ngOnInit(): void {
        this.getUsers('active')
    }

    onDelBtnClick(user: User) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "禁用账号: " + user.name
        ref.componentInstance.message = "确认要禁用吗?"
        ref.componentInstance.detail = "此操作将把账号'" + user.name + "'标记为禁用状态，不会直接删除用户及其相关资源数据"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.blockUser(user).subscribe(ret => {
                    if (ret.code == 0) {
                        this.userList = this.userList.filter(it => it.id != user.id)
                        this.alert.success('禁用账号成功')
                    }
                })
            }
        }, resaon => { })
    }

    onUnblockBtnClick(user: User) {}

    hiddenUnblockBtn() {
        return true
    }

    getDelBtnLabel() {
        return '禁用'
    }

}


@Component({
    selector: 'blocked-users',
    templateUrl: './users.component.html'
})
export class BlockedUsersComponent extends UsersComponent implements OnInit {

    constructor(
        public svc: UsersService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal) {
        super(svc, alert, modal)
    }

    ngOnInit(): void {
        this.getUsers('blocked')
    }

    onDelBtnClick(user: User) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除账号: " + user.name
        ref.componentInstance.message = "确认要删除吗?"
        ref.componentInstance.detail = "此操作将删除账号'" + user.name + "',此操作不可恢复请谨慎操作！"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteUser(user).subscribe(ret => {
                    if (ret.code == 0) {
                        this.userList = this.userList.filter(it => it.id != user.id)
                        this.alert.success('删除账号成功')
                    }
                })
            }
        }, resaon => {})
    }

    onUnblockBtnClick(user: User) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "启用账号: " + user.name
        ref.componentInstance.message = "确认要启用吗?"
        ref.componentInstance.detail = "此操作将启用账号'" + user.name + "',用户可以正常登陆并访问或操作被授权的资源"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.unblockUser(user).subscribe(ret => {
                    if (ret.code == 0) {
                        this.userList = this.userList.filter(it => it.id != user.id)
                        this.alert.success('启用账号成功')
                    }
                })
            }
        }, resaon => { })
    }

    hiddenUnblockBtn() {
        return false
    }

    getDelBtnLabel() {
        return '删除'
    }

}
