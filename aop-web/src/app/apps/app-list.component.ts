import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppsService } from './apps.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router } from '@angular/router';

@Component({
    selector: 'apps',
    templateUrl: './app-list.component.html'
})
export class AppListComponent implements OnInit {

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private router: Router) {
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    search(event: FormDataEvent) {
        event.preventDefault();
        //let pre = this.searchForm.get('searchPrefix').value;
    }


    ngOnInit(): void {
    }

    onEditBtnClick(app: App) {
        this.svc.app = app;
        this.router.navigate(['/apps/'+app.id+'/edit'])
    }

    onViewBtnClick(app: App) {
        this.svc.app = app;
        this.router.navigate(['/apps', app.id])
    }

    onDelBtnClick(app: App) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "删除应用: "+app.apptag;
        ref.componentInstance.message = "确认要删除吗?"
        ref.componentInstance.detail = "此操作将暂时把应用'"+app.apptag+"'标记为删除状态，其相关配置与文件会在之后的定时维护任务中集中删除"
        ref.result.then(result => {
          if (result == "ok") {
            this.svc.deleteApp(app.id).subscribe(succeed => {
              if (succeed) {
                this.alert.success('删除应用成功');
              }
            });
          }
        }, resaon => {})
    }

    onNewBtnClick() {
        this.svc.app = this.svc.newTomcatApp();
        this.router.navigate(['/apps/new/edit'])
    }

}
