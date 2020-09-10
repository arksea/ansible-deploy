import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { UsersService } from '../users/users.service';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../users/users.entity';


@Component({
    selector: 'group-members',
    templateUrl: './group-members.component.html'
})
export class GroupMembersComponent implements OnInit {

    public model: any;

    searchUser = (text: Observable<string>) => this.svc.search(text, '', this.usersSvc.userList.pipe(map(users => {
        let names: string[] = [];
        for (let u of users) {
            names.push(u.name)
        }
        return names;
    })));

    constructor(public svc: GroupsService,
        public account: AccountService,
        public usersSvc: UsersService,
        private route: ActivatedRoute,
        private router: Router,
        private alert: MessageNotify,
        private modal: NgbModal) {
            this.usersSvc.getUsers('active');
    }

    ngOnInit() { }

    addMember(name: string) {
        this.usersSvc.getUserByName(name).subscribe(u => {
            if (u == null) {
                this.alert.warning('未找到用户:'+name);
            } else {
                this.svc.addMember(u).subscribe(succeed => {
                    if (succeed)this.alert.info('添加成员成功');
                })
            }
        })
    }

    removeMember(user: User) {
        return this.svc.removeMember(user).subscribe(succeed => {
            if (succeed) {
                this.alert.info('移除主机成功');
            }
        });
    }
}
