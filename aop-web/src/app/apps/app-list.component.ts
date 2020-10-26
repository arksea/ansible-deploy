import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { App } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router } from '@angular/router'

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
        //let pre = this.searchForm.get('searchPrefix').value
    }

    ngOnInit(): void {
    }

    onEditBtnClick(app: App) {
        this.router.navigate(['/apps/' + app.id + '/edit'])
    }

    onViewBtnClick(app: App) {
        this.router.navigate(['/apps', app.id])
    }

    onDelBtnClick(app: App) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除应用: " + app.apptag
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteApp(app.id).subscribe(succeed => {
                    if (succeed) {
                        app.deleted = true
                        this.alert.success('已删除')
                    }
                })
            }
        }, resaon => { })
    }

    onNewBtnClick(appType: string) {
        this.router.navigate(['/apps/new/edit', appType])
    }

    appHasTargetHosts(app: App): boolean {
        for (let v of app.versions) {
            if (v.targetHosts.length > 0) {
                return true
            }
        }
        return false
    }
}
