import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { AppTypesService } from './app-types.service';
import { MessageNotify } from '../utils/message-notify';
import { App,AppGroup, Host, AppVariable, AppVarDefine } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { HostsService } from '../hosts/hosts.service';
import { Version } from '../app.entity';
import { ConfirmDialog } from '../utils/confirm.dialog';


@Component({
    selector: 'app-type-edit',
    templateUrl: './app-type-edit.component.html'
})
export class AppTypeEditComponent implements OnInit {

    public typeForm: FormGroup = new FormGroup({
        name: new FormControl('', [Validators.required, Validators.maxLength(32)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(128)])
    })

    constructor(private svc: AppTypesService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
    }

    save() {
    }

    get name() {
        return this.typeForm.get('name')
    }

    get description() {
        return this.typeForm.get('description')
    }

}
