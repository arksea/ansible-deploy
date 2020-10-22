import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppTypesService } from './app-types.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { App } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-type-list',
    templateUrl: './app-type-list.component.html'
})
export class AppTypeListComponent implements OnInit {

    constructor(
        public svc: AppTypesService,
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
        this.router.navigate(['/apps/'+app.id+'/edit'])
    }

    onViewBtnClick(app: App) {
        this.router.navigate(['/apps', app.id])
    }

    onDelBtnClick(app: App) {
        // if (app.deleted) {
        //     this.doUndelete(app);
        // } else {
        //     this.doDelete(app);
        // }
    }
    // private doDelete(app: App) {
    //     let ref = this.modal.open(ConfirmDialog);
    //     ref.componentInstance.title = "删除应用: "+app.apptag;
    //     ref.componentInstance.message = "确认要删除吗?"
    //     ref.componentInstance.detail = "此操作将暂时把应用'"+app.apptag+"'标记为删除状态，其相关配置与文件会在之后的定时维护任务中集中删除"
    //     ref.result.then(result => {
    //       if (result == "ok") {
    //         this.svc.updateDeleted(app.id, true).subscribe(succeed => {
    //           if (succeed) {
    //             app.deleted = true;
    //             this.alert.success('已标记为删除状态');
    //           }
    //         });
    //       }
    //     }, resaon => {})
    // }

    // private doUndelete(app: App) {
    //     let ref = this.modal.open(ConfirmDialog);
    //     ref.componentInstance.title = "恢复应用: "+app.apptag;
    //     ref.componentInstance.message = "确认要恢复吗?"
    //     ref.componentInstance.detail = "此操作将取消应用'"+app.apptag+"'的删除状态"
    //     ref.result.then(result => {
    //       if (result == "ok") {
    //         this.svc.updateDeleted(app.id, false).subscribe(succeed => {
    //           if (succeed) {
    //             app.deleted = false;
    //             this.alert.success('恢复成功');
    //           }
    //         });
    //       }
    //     }, resaon => {})
    // }

    onNewBtnClick(appType: string) {
        this.router.navigate(['/app-types/new/edit',appType])
    }

}
