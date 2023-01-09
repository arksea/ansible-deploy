import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppTypesService } from './app-types.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { App, AppType } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router } from '@angular/router'

@Component({
    selector: 'app-type-list',
    templateUrl: './app-type-list.component.html'
})
export class AppTypeListComponent implements OnInit {

    appTypes: Array<AppType> = [];

    constructor(
        public svc: AppTypesService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private router: Router) {
            this.svc.getAppTypes().subscribe(
                ret => {
                    if (ret.code == 0) {
                        this.appTypes = ret.result
                    }
                }
            )
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
        this.router.navigate(['/apps/' + app.id + '/edit'])
    }

    onViewBtnClick(app: App) {
        this.router.navigate(['/apps', app.id])
    }

    onDelBtnClick(appType: AppType) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "确认要删除吗?"
        ref.componentInstance.message = "删除应用类型: "+appType.name;
        ref.componentInstance.detail = "操作将删除此应用类型，删除前请先确认没有此类型下没有创建应用实例，或者删除所有相关应用"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteAppType(appType.id).subscribe(ret => {
                    if (ret.code == 0) {
                        let newAppTypes = this.appTypes.filter((t,index,list) => {
                                return t.id != appType.id
                        })
                        this.appTypes = newAppTypes;
                        this.alert.success('已删除应用类型：'+appType.name);
                    }
            });
          }
        }, resaon => {})


        
    }
    // private doDelete(app: App) {

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
        this.router.navigate(['/app-types/new/edit', appType])
    }

}
