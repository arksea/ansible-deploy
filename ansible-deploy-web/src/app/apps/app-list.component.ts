import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { PageEvent } from '@angular/material/paginator';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { AppListService } from './app-list.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { App, AppType, Page } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router } from '@angular/router'
import { DeleteJobPlayDialog } from './job-play.dialog'

@Component({
    selector: 'apps',
    templateUrl: './app-list.component.html'
})
export class AppListComponent implements OnInit {


    constructor(
        public svc: AppListService, 
        public api: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private router: Router) {
            this.searchForm.get('searchPrefix').setValue(this.svc.searchPrefix)
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    search(event: FormDataEvent) {
        //event.preventDefault();
        let search = this.searchForm.get('searchPrefix').value
        this.svc.searchPrefix = search
        this.svc.query()
    }

    ngOnInit(): void {
    }

    onEditBtnClick(app: App) {
        this.router.navigate(['/apps/' + app.id + '/edit'])
    }

    onVersionBtnClick(app: App) {
        this.router.navigate(['/apps/' + app.id + '/versions'])
    }

    onViewBtnClick(app: App) {
        this.router.navigate(['/apps', app.id])
    }

    private confirmDeleteApp(app: App) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "删除应用: " + app.apptag
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteApp(app)
            }
        }, resaon => { })
    }

    onDelBtnClick(app: App) {
        this.api.getOperationsByAppTypeId(app.appType.id).subscribe(ret => {
            if (ret.code == 0) {
                let operations = ret.result
                let hasDelScript = false
                for (let op of operations) {
                    if (op.type == 'DELETE_APP') {
                        hasDelScript = true
                        let ref = this.modal.open(DeleteJobPlayDialog, {size: 'lg', scrollable: true})
                        ref.componentInstance.operation = op
                        ref.componentInstance.app = app
                        ref.componentInstance.hosts = []
                        ref.result.then(result => {
                                if (result == 'skip') {
                                    this.confirmDeleteApp(app)
                                } else if (result == "ok") {
                                    this.svc.deleteApp(app)
                                }
                            }, reason => {
                                this.alert.error("删除失败: " + reason)
                            })
                        break
                    }
                }
                if (!hasDelScript)  {
                    this.confirmDeleteApp(app)
                }
            }
        })
    }

    onNewBtnClick(appType: AppType) {
        this.router.navigate(['/apps/new/edit', appType.name])
    }

    onShowHisBtnClick(app: App) {
        this.router.navigate(['/apps', app.id, 'ophis'])
    }

    appHasTargetHosts(app: App): boolean {
        for (let v of app.versions) {
            if (v.targetHosts.length > 0) {
                return true
            }
        }
        return false
    }

    public onPageEvent(event: PageEvent): PageEvent {
        this.svc.pageSize = event.pageSize
        this.svc.searchPrefix = this.searchForm.get('searchPrefix').value
        this.svc.pageIndex = event.pageIndex
        this.svc.query()
        return event;
    }
}
