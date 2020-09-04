import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App, AppGroup, GroupVar } from '../app.entity';
import { AccountService } from '../account/account.service';
import { map, flatMap, publishReplay, refCount } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { format } from 'url';
import { NewVersionDialog } from './new-version.dialog';


@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    public svnaddr = 'svn://127.0.0.1';
    private appId: number;
    constructor(public svc: AppsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private alert: MessageNotify,
                private modal: NgbModal) {
        this.appId = Number(this.route.snapshot.paramMap.get('id'));
        this.svc.setSelectedApp(this.appId);
    }

    ngOnInit() {}

    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog);
        ref.componentInstance.appId = this.appId;
    }

    onEditBtnClick() {

    }

    onDelBtnClick() {

    }
}