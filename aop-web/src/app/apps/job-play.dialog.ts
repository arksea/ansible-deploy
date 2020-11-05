import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { Subject, BehaviorSubject, Observable } from 'rxjs'
import { publishReplay, refCount, scan, map, delay } from 'rxjs/operators'
import { AppsService } from './apps.service'
import { AppOperation, App, Host, Version } from '../app.entity'

export type IModelOperation = (data: ModelData) => ModelData
export class ModelData {
    constructor(public jobId: number, public logs: string, public index: number, public size: number, public pollCount: number) {
    }
}
const JOB_LOGS_MAX_POLL_COUNT = 300

@Component({
    selector: 'job-play-dialog',
    templateUrl: './job-play.dialog.html'
})
export class JobPlayDialog implements OnInit, OnDestroy, AfterViewChecked {

    operation: AppOperation
    app: App
    hosts: Array<Host>
    ver: Version

    @ViewChild('scrollLogs')
    private scrollContainer: ElementRef



    jobStarted: BehaviorSubject<boolean> = new BehaviorSubject(false)
    // opAddModel     ──┬──＞ updates  ===＞ jobLogs
    opAddModel: Subject<ModelData> = new Subject()
    updates: Subject<IModelOperation> = new Subject()
    jobLogs: Observable<ModelData>
    isStatusTestJob: boolean = false

    constructor(public modal: NgbActiveModal, public svc: AppsService) {
    }

    ngAfterViewChecked() {        
        this.scrollToBottom();        
    } 
    
    scrollToBottom(): void {
        try {
            this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
        } catch(err) {
        }                 
    }

    ngOnInit(): void {
        this.jobLogs = this.updates.pipe(
            scan((modelData: ModelData, op: IModelOperation) => {
                const newData = op(modelData)
                return newData
            }, new ModelData(0,'',0,0,0)),
            publishReplay(1),
            refCount()
        )
        this.opAddModel.pipe(
            delay(100),
            map(function (newData: ModelData): IModelOperation {
                return (oldData: ModelData) => {
                    window.scrollTo(0, 9999)
                    return newData
                }
            })
        ).subscribe(this.updates)
        this.opAddModel.pipe(delay(1000)).subscribe(data => {
            if (data.index != -1 && data.pollCount < JOB_LOGS_MAX_POLL_COUNT) {
                this.svc.pollJobLogs(data.jobId, data.index).subscribe(s => {
                    if (s.code == 0) {
                        let ret = s.result
                        this.setHostStatus(ret.log)
                        this.opAddModel.next(new ModelData(data.jobId, data.logs+ret.log, ret.index, ret.size, data.pollCount+1))
                    }
                })
            }
            if (data.index == -1 && this.isStatusTestJob) {
                this.modal.close('ok')
            }
        })
    }

    ngOnDestroy(): void {
    }

    startJob() {
        this.svc.startJob(this.app,this.ver, this.operation, this.hosts).subscribe(ret => {
            if (ret.code == 0) {
                this.jobStarted.next(true)
                let jobId = ret.result.id
                this.svc.pollJobLogs(jobId, 0).subscribe(ret => {
                    if (ret.code == 0) {
                        this.opAddModel.next(new ModelData(jobId, ret.result.log, ret.result.index, ret.result.size, 1))
                    }
                })
            }
        })
    }

    private setHostStatus(log: string) {
        let lines = log.split('\n')
        let method = '@@SET_HOST_STATUS'
        for (let line of lines) {
            let i = line.indexOf(method)
            if (i >= 0) {
                let kvStr = line.substring(i+method.length+1)
                let kv = kvStr.split(',')
                if (kv.length == 3) {
                    let key   = kv[0]
                    let value = kv[1]
                    let color = Number(kv[2])
                    for (let h of this.hosts) {
                        if (h.privateIp == key) {
                            h.status[this.operation.name] = {value: value, color: color}
                        }
                    }
                }
            }
        }
    }
}

@Component({
    selector: 'status-job-play-dialog',
    templateUrl: './job-play.dialog.html'
})
export class StatusJobPlayDialog extends JobPlayDialog {
    constructor(public modal: NgbActiveModal, public svc: AppsService) {
        super(modal, svc)
        this.isStatusTestJob = true
    }
    ngOnInit(): void {
        super.ngOnInit()
        this.startJob()
    }
};