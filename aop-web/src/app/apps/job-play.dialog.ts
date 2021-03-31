import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { Subject, BehaviorSubject, Observable } from 'rxjs'
import { publishReplay, refCount, scan, map, delay } from 'rxjs/operators'
import { AppsService } from './apps.service'
import { AppOperation, App, Host, Version, OperationVariable } from '../app.entity'

export type IModelOperation = (data: ModelData) => ModelData
export class ModelData {
    constructor(public jobId: number, public logs: string, public index: number, public size: number, public pollCount: number) {
    }
}
const JOB_LOGS_MAX_POLL_COUNT = 300

@Component({
    selector: 'job-play-dialog',
    templateUrl: './job-play.dialog.html',
    styles: ['#job-logs {min-height: 400px;}']
})
export class JobPlayDialog implements OnInit, OnDestroy, AfterViewChecked {

    operation: AppOperation
    app: App
    hosts: Array<Host>
    ver: Version
    public form: FormGroup

    @ViewChild('scrollLogs')
    private scrollContainer: ElementRef

    public isDeleteJob: boolean = false

    jobStarted: BehaviorSubject<boolean> = new BehaviorSubject(false)
    // opAddModel     ──┬──＞ updates  ===＞ jobLogs
    opAddModel: Subject<ModelData> = new Subject()
    updates: Subject<IModelOperation> = new Subject()
    jobLogs: Observable<ModelData>

    constructor(public modal: NgbActiveModal, public svc: AppsService) {
        this.form = new FormGroup({})
    }

    setParams(op: AppOperation, app: App, ver: Version, hosts: Array<Host>): void {
        this.operation = op
        this.app = app
        this.ver = ver
        this.hosts = hosts
        for (let v of op.varDefines) {
            this.form.addControl('var_' + v.name, new FormControl(v.defaultValue, [Validators.maxLength(128)]))
        }
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
        })
    }

    ngOnDestroy(): void {
    }

    startJob() {
        let vars: Array<OperationVariable> = []
        for (let i of this.operation.varDefines) {
            let c = this.form.get('var_' + i.name)
            let v = new OperationVariable()
            v.name = i.name
            v.value = c.value
            vars.push(v)
        }
        this.svc.startJob(this.app,this.ver, this.operation, this.hosts, vars).subscribe(ret => {
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

    skipJob() {
        this.modal.close('skip')
    }

    private setHostStatus(log: string) {
        let lines = log.split('\n')
        let method = '@@SET_HOST_STATUS'
        for (let line of lines) {
            let i = line.indexOf(method)
            if (i >= 0) {
                let kvStr = line.substring(i+method.length+1)
                let kv = kvStr.split(',')
                if (kv.length > 4) {
                    let key   = kv[0]
                    let value = kv[1]
                    let color = kv[2]
                    let name = kv[3]
                    for (let h of this.hosts) {
                        if (h.privateIp == key) {
                            h.status[name] = {value: value, color: color}
                        }
                    }
                } else if (kv.length > 3) {
                    let key   = kv[0]
                    let value = kv[1]
                    let color = kv[2]
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
    templateUrl: './job-play.dialog.html',
    styles: ['#job-logs {min-height: 400px;}']
})
export class StatusJobPlayDialog extends JobPlayDialog {
    constructor(public modal: NgbActiveModal, public svc: AppsService) {
        super(modal, svc)
    }
    ngOnInit(): void {
        super.ngOnInit()
        if (this.operation.varDefines.length == 0) {
            this.startJob()
        }
    }
}

@Component({
    selector: 'delete-job-play-dialog',
    templateUrl: './job-play.dialog.html',
    styles: ['#job-logs {min-height: 400px;}']
})
export class DeleteJobPlayDialog extends JobPlayDialog {
    constructor(public modal: NgbActiveModal, public svc: AppsService) {
        super(modal, svc)
        this.isDeleteJob = true
    }
    ngOnInit(): void {
        super.ngOnInit()
    }
}