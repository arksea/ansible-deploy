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


@Component({
    selector: 'app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
    public app: Observable<App>;
    public svnaddr = 'svn://127.0.0.1';
    constructor(public svc: AppsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private alert: MessageNotify) {
        if (this.svc.selectedApp.id == null) {
            let id: number = Number(this.route.snapshot.paramMap.get('id'));
            this.app = this.svc.getAppById(id).pipe(map (
                resp => {
                    if (resp.code == 0) {
                        return resp.result;
                    } 
                }
            ))
        } else {
            this.app = new BehaviorSubject(this.svc.selectedApp);
        }
    }

    ngOnInit() {

    }

    onNewVersionBtnClick() {
        this.router.navigate(['/apps/edit/ver'])
    }

    onEditBtnClick() {

    }

    onDelBtnClick() {

    }
}