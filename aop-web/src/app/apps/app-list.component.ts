import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { PageEvent } from '@angular/material/paginator';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { App, AppType, UserAppsPage } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router } from '@angular/router'
import { JobPlayDialog } from './job-play.dialog'

@Component({
    selector: 'apps',
    templateUrl: './app-list.component.html'
})
export class AppListComponent implements OnInit {
    pageSize: number = 7
    appList : UserAppsPage = new UserAppsPage()
    appTypes: AppType[] = []

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private router: Router) {
            this.query(0,'')
            this.svc.getAppTypes().subscribe(ret => {
                if (ret.code == 0) {
                    this.appTypes = ret.result
                }
            })
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    search(event: FormDataEvent) {
        //event.preventDefault();
        let search = this.searchForm.get('searchPrefix').value
        this.query(0, search)
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
                this.doDeleteOperation(app)
            }
        }, resaon => { })
    }

    private doDeleteOperation(app: App) {
        this.svc.getOperationsByAppTypeId(app.appType.id).subscribe(ret => {
            if (ret.code == 0) {
                let operations = ret.result
                let hasDelScript = false
                for (let op of operations) {
                    if (op.type == 'DELETE_APP') {
                        hasDelScript = true
                        let ref = this.modal.open(JobPlayDialog, {size: 'lg', scrollable: true})
                        ref.componentInstance.operation = op
                        ref.componentInstance.app = app
                        ref.componentInstance.hosts = []
                        ref.result.then(result => {
                                if (result == "ok") {
                                    this.doDeleteApp(app)
                                }
                            }, reason => {
                                this.alert.error("删除失败: " + reason)
                            })
                        break
                    }
                }
                if (!hasDelScript)  {
                    this.doDeleteApp(app)
                }
            }
        })
    }

    private doDeleteApp(app: App) {
        this.svc.deleteApp(app.id).subscribe(succeed => {
            if (succeed) {
                this.appList.items = this.appList.items.filter(it => it.id != app.id)
                this.alert.success('已删除')
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
        let page = event.pageIndex + 1;
        this.pageSize = event.pageSize;
        let search = this.searchForm.get('searchPrefix').value
        this.query(page, search)
        return event;
    }

    private query(page: number, nameSearch: string) {
        this.svc.getUserApps(page, this.pageSize, nameSearch).subscribe(ret => {
            if (ret.code == 0) {
                this.appList = ret.result
            }
        })
    }
}
