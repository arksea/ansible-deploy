import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AppsService } from '../apps/apps.service';

@Component({
    selector: 'group-apps',
    templateUrl: './group-apps.component.html'
})
export class GroupAppsComponent implements OnInit {
    public model: any;

    search = (text: Observable<string>) => this.svc.search(text, '没有未分组的应用', this.appSvc.appList.pipe(map(list => {
        let names: string[] = [];
        for (let i of list) {
            names.push(i.apptag)
        }
        return names;
    })));
    constructor(public svc: GroupsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private appSvc: AppsService,
                private router: Router,
                private alert: MessageNotify,
                private modal: NgbModal) {
        appSvc.queryNotInGroupApps();
    }


    ngOnInit() {}

    add() {}
}
