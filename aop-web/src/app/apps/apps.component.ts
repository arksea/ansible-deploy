import { Component, OnInit } from '@angular/core';
import { AppsService } from './apps.service';

@Component({
    selector: 'apps',
    templateUrl: './apps.component.html'
})
export class AppsComponent implements OnInit {

    constructor(private svc: AppsService) {
        this.svc.queryVarDefine();
    }

    ngOnInit(): void {
    }
}
