import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { FormGroup, FormControl, AbstractControl } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, OperationJobInfo, Page, Version } from '../app.entity'
import { AccountService } from '../account/account.service'
import { PageEvent } from '@angular/material/paginator'
import { JobLogDialog } from './job-log.dialog'
import { JobPlayDialog } from './job-play.dialog'

@Component({
    selector: 'app-operation-history',
    templateUrl: './app-operation-history.component.html'
})
export class AppOperationHistoryComponent implements OnInit {
    pageSize: number = 14
    app: App = new App()
    public form: FormGroup = new FormGroup({
        startTime: new FormControl({ value: '', disabled: true }),
        endTime: new FormControl({ value: '', disabled: true }),
        operator: new FormControl('')
    });
    public history: Page<OperationJobInfo> = new Page()

    constructor(private svc: AppsService,
        public account: AccountService,
        private route: ActivatedRoute,
        private router: Router,
        private modal: NgbModal,
        private alert: MessageNotify) {
        let appId = Number(this.route.snapshot.paramMap.get('id'))
        this.svc.getAppById(appId).subscribe(ret => {
            if (ret.code == 0) {
                this.app = ret.result
                this.query(0)
            } else {
                this.alert.warning("应用不存在或无权限(id=" + appId + ")")
                this.router.navigate(["/apps"])
            }
        })
    }

    ngOnInit() { }

    public query(page: number) {
        const start = this.startTime.value ? new Date(this.startTime.value).toISOString() : ''
        const end = this.endTime.value ? new Date(this.endTime.value).toISOString() : ''
        const operator = this.operator.value
        this.svc.getAppOperationJobHistory(this.app.id, page, this.pageSize, start, end, operator).subscribe(ret => {
            if (ret.code == 0) {
                this.history = ret.result
            }
        })
    }

    public onPageEvent(event: PageEvent): PageEvent {
        let page = event.pageIndex + 1;
        this.pageSize = event.pageSize;
        this.query(page)
        return event;
    }

    public onJobClick(jobInfo: OperationJobInfo) {
        if (jobInfo.endTime != null) {
            let ref = this.modal.open(JobLogDialog, {size: 'xl', scrollable: true})
            ref.componentInstance.jobInfo = jobInfo
        } else {
            let ref = this.modal.open(JobPlayDialog, {size: 'xl', scrollable: true, backdrop: 'static', keyboard: false})
            let ver: Version
            for (let v of this.app.versions) {
                if (jobInfo.version == v.name) {
                    ver = v
                    break
                }
            }
            ref.componentInstance.setParams({name:jobInfo.operation, varDefines: []}, this.app, ver, ver.targetHosts)
            ref.componentInstance.onJobStarted(jobInfo.jobId)
        }
    }

    public get operator(): AbstractControl {
        return this.form.get('operator')
    }

    public get startTime(): AbstractControl {
        return this.form.get('startTime')
    }

    public get endTime(): AbstractControl {
        return this.form.get('endTime')
    }
}
