import { Component, OnInit } from '@angular/core';
import { FormGroup,FormControl } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UsersService } from './users.service';
import { AccountService } from '../account/account.service';
import { User } from './users.entity';
import { AppGroup } from '../app.entity'

@Component({
    selector: 'user-groups-dialog',
    templateUrl: './user-groups.dialog.html'
})
export class UserGroupsDialog implements OnInit {

    _user: User
    userGroups: Array<AppGroup> = [];
    public form: FormGroup = new FormGroup({})

    constructor(public modal: NgbActiveModal, public svc: UsersService, public account: AccountService) {
        let p = this.account.perm('组管理:修改') || this.account.perm('用户管理:修改')
        for (let g of this.svc.groups) {
            this.form.addControl('group-'+g.id,new FormControl({value:false, disabled: p}))
        }
    }

    ngOnInit(): void {
    }

    get user() {
        return this._user
    }
    set user(u: User) {
        this._user = u
        this.svc.getUserGroups(u.id).subscribe(ret => {
            if (ret.code == 0) {
                this.userGroups = ret.result
                for (let g of this.userGroups) {
                    let c = this.form.get('group-'+g.id)
                    if (c) {
                        c.setValue(true)
                    }
                }
            }
        })
    }

    save() {
        let groups: Array<AppGroup> = []
        let ids: Array<number> = []
        for (let g of this.svc.groups) {
            let c = this.form.get('group-'+g.id)
            if (c.value) {
                ids.push(g.id)
                groups.push(g)
            }
        }
        this.svc.updateUserGroups(this._user.id, ids).subscribe(ret => {
            if (ret.code == 0) {
                this.modal.close('ok')
            } else {
                this.modal.close('failed')
            }
        });
    }

    
}
