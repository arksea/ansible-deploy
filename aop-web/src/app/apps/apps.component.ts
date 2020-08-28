import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App } from '../app.entity';
import { AccountService } from '../account/account.service';

@Component({
    selector: 'apps',
    templateUrl: './apps.component.html'
})
export class AppsComponent implements OnInit {

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal) {
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    search(event: FormDataEvent) {
        event.preventDefault();
        //let pre = this.searchForm.get('searchPrefix').value;
    }

    newApp() {
        //this.modal.open(NewUserDialog);
    }

    ngOnInit(): void {
        this.account.loginUser.subscribe(
            user => this.svc.getUserApps(user)
        )
    }

    onEditBtnClick(app: App) {

    }

}
