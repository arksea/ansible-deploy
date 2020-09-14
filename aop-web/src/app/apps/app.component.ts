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
    appId: number;
    app: Observable<App>;
    constructor(private svc: AppsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private modal: NgbModal,
                private alert: MessageNotify) {
        this.appId = Number(this.route.snapshot.paramMap.get('id'));
        this.app = this.svc.getAppById(this.appId).pipe(map(a => {
            if (a == null) {
                this.alert.warning("没有查询到应用("+this.appId+")，已跳转到应用列表");
                this.router.navigate(["/apps"]);
            }
            return a;
        }));
    }

    ngOnInit() {}

    onNewVersionBtnClick() {
        let ref = this.modal.open(NewVersionDialog);
        ref.componentInstance.appId = this.appId;
        ref.componentInstance.app = this.app;
    }

    onEditBtnClick() {

    }

    onDelBtnClick() {

    }
}