import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { App, AppType } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router } from '@angular/router'
import { JobPlayDialog } from './job-play.dialog'

@Component({
    selector: 'apps',
    templateUrl: './app-list.component.html'
})
export class AppListComponent implements OnInit {

    appList : App[] = []
    appTypes: AppType[] = []

    constructor(
        public svc: AppsService,
        public account: AccountService,
        protected alert: MessageNotify,
        protected modal: NgbModal,
        private router: Router) {
            this.svc.getUserApps().subscribe(ret => {
                if (ret.code == 0) {
                    this.appList = ret.result
                }
            })
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
                this.appList = this.appList.filter(it => it.id != app.id)
                this.alert.success('已删除')
            }
        })
    }

    onNewBtnClick(appType: AppType) {
        this.router.navigate(['/apps/new/edit', appType.name])
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
