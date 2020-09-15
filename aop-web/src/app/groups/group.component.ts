import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';

@Component({
    selector: 'group',
    templateUrl: './group.component.html'
})
export class GroupComponent implements OnInit {

    groupId: number;
    constructor(public svc: GroupsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private alert: MessageNotify,
                private modal: NgbModal) {
        this.groupId = Number(this.route.snapshot.paramMap.get('id'));
        this.svc.setSelectedGroup(this.groupId);
        this.router.navigate(['/groups/'+this.groupId+'/members']);
    }

    ngOnInit() {

    }
}
