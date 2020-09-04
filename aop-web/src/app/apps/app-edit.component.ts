import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { MessageNotify } from '../utils/message-notify';
import { App, AppGroup } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AppEditService } from './app-edit.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-edit',
    templateUrl: './app-edit.component.html'
})
export class AppEditComponent implements OnInit {

    constructor(public svc: AppsService,
                public editSvc: AppEditService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        let str = this.route.snapshot.paramMap.get('id');
        if (str == 'new') {
            let app = this.svc.newTomcatApp();
            this.svc.setSelectedApp(app.id);
        } else {
            let id = Number(str);
            this.svc.setSelectedApp(id);
        }
    }

    public save(event: FormDataEvent) {
        event.preventDefault(); //取消submit事件的默认处理：刷新页面
        this.editSvc.opSaveApp.next(true);
    }

    public cancel() {
        this.router.navigate(['/apps'])
    }

}
