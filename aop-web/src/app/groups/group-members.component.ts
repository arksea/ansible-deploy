import { Component, OnInit } from '@angular/core'
import { GroupsService } from './groups.service'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from '../account/account.service'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators'
import { User } from '../users/users.entity'
import { AppGroup } from '../app.entity'


@Component({
    selector: 'group-members',
    templateUrl: './group-members.component.html'
})
export class GroupMembersComponent implements OnInit {

    public model: any
    public group: AppGroup

    searchUser = (text: Observable<string>) => this.svc.search(text, this.svc.getUsersNotInCurrentGroup().pipe(
        map(ret => ret.code == 0 ? ret.result.map(it => it.name) : [])  
    ))

    constructor(private svc: GroupsService,
            private account: AccountService,
            private alert: MessageNotify) {
        this.svc.getCurrentGroup().subscribe(ret => {
            if (ret.code == 0) {
                this.group = ret.result
            }
        })
    }

    ngOnInit() {}

    addMember(name: string) {
        this.model = ''
        this.svc.getUserByName(name).subscribe(ret => {
            if (ret.code == 0) {
                if (ret.result == null) {
                    this.alert.warning('未找到用户:'+name)
                } else {
                    let u: User = ret.result
                    this.svc.addMember(u).subscribe(ret => {
                        if (ret.code == 0) {
                            this.group.users.push(u)
                            this.alert.success('添加成员成功')
                        }
                    })                    
                }
            }
        })
    }

    removeMember(user: User) {
        return this.svc.removeMember(user).subscribe(ret => {
            if (ret.code == 0) {
                this.group.users = this.group.users.filter(it => it.id != user.id)
                this.alert.success('移除成员成功')
            }
        })
    }

    public perm(name: string): boolean {
        return this.account.perm(name)
    }
}
