import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GroupsService } from './groups.service';

@Component({
    selector: 'group',
    templateUrl: './group.component.html'
})
export class GroupComponent {
    groupId: number;
    constructor(public svc: GroupsService, private route: ActivatedRoute) {
        this.groupId = Number(this.route.snapshot.paramMap.get('id'));
        this.svc.setSelectedGroup(this.groupId);
    }
}
