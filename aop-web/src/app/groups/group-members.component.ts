import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { UsersService } from '../users/users.service';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';


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

    addMember() {

    }
}
