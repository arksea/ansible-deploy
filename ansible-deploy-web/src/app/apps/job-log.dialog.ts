import { Component, OnInit, OnDestroy } from '@angular/core'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { OperationJobInfo } from '../app.entity'


@Component({
    selector: 'job-log-dialog',
    templateUrl: './job-log.dialog.html',
    styles: ['#job-logs {min-height: 400px;}']
})
export class JobLogDialog implements OnInit, OnDestroy {

    jobInfo: OperationJobInfo
    log: string


    constructor(public modal: NgbActiveModal, public svc: AppsService) {
    }



    ngOnInit(): void {
        this.svc.getJobHistoryLogs(this.jobInfo.jobId).subscribe(ret => {
            if (ret.code == 0) {
                this.log = ret.result
            }
        });
    }

    ngOnDestroy(): void {
    }
}
