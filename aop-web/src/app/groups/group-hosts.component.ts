import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { debounceTime,distinctUntilChanged,map } from 'rxjs/operators';
import { Subject, Observable } from 'rxjs';

@Component({
    selector: 'group-hosts',
    templateUrl: './group-hosts.component.html'
})
export class GroupHostsComponent implements OnInit {

    // public model: any;
    // //public search = this.svc.searchHost;

    // public hosts: string[] = ['xiaohaixing','liuyawen','fengbin'];

    // search = (text: Observable<string>) => {
    //     text.pipe(
    //         debounceTime(200),
    //         distinctUntilChanged(),
    //         map( term => {
    //             if (term.length < 2) {
    //                 return []
    //             } else {
    //                 return this.hosts.filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10);
    //             }
    //         })
    // )}

    constructor(public svc: GroupsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private alert: MessageNotify,
                private modal: NgbModal) {

    }

    ngOnInit() {}

    addHost() {
        
    }
}
