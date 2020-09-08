import { Component, OnInit } from '@angular/core';
import { GroupsService } from './groups.service';

@Component({
    selector: 'groups',
    templateUrl: './groups.component.html'
})
export class GroupsComponent implements OnInit {

    constructor(private svc: GroupsService) {
    }

    ngOnInit(): void {
        this.svc.queryGroups();
    }
}
