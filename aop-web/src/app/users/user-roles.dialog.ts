import { Component, OnInit } from '@angular/core';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UsersService } from './users.service';
import { AccountService } from '../account/account.service';
import { MessageNotify } from '../utils/message-notify';
import { User, Role } from './users.entity';

@Component({
    selector: 'user-roles-dialog',
    templateUrl: './user-roles.dialog.html'
})
export class UserRolesDialog implements OnInit {

    _user: User
    public form: FormGroup = new FormGroup({})

    constructor(public modal: NgbActiveModal, public svc: UsersService, public account: AccountService) {
        this.account.perm('用户管理:修改').subscribe(
            s => {
                for (let r of this.svc.roles) {
                    this.form.addControl('role-'+r.id,new FormControl({value:false, disabled: s}))
                }
            }
        )
    }

    ngOnInit(): void {
    }

    set user(u: User) {
        this._user = u
        for (let r of u.roles) {
            let c = this.form.get('role-'+r.id)
            if (c) {
                c.setValue(true)
            }
        }
    }

    save() {
        let roles: Array<Role> = []
        let ids: Array<number> = []
        for (let r of this.svc.roles) {
            let c = this.form.get('role-'+r.id)
            if (c.value) {
                ids.push(r.id)
                roles.push(r)
            }
        }
        this.svc.updateUserRoles(this._user.id, ids).subscribe(ret => {
            if (ret.code == 0) {
                this._user.roles = roles
                this.modal.close('ok')
            }
        });
    }

    
}
