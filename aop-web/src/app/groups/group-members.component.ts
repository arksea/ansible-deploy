import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { debounceTime,distinctUntilChanged,map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
    selector: 'group-members',
    templateUrl: './group-members.component.html'
})
export class GroupMembersComponent implements OnInit {

    public users: string[] = ['xiaohaixing','liuyawen','fengbin'];
    public model: any;

    search = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(200),
        distinctUntilChanged(),
        map(term => term.length < 2 ? []
          : this.users.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
      )
    constructor(public svc: GroupsService,
        public account: AccountService,
        private route: ActivatedRoute,
        private router: Router,
        private alert: MessageNotify,
        private modal: NgbModal) {

    }

    ngOnInit() { }

    addMember() {

    }
}
