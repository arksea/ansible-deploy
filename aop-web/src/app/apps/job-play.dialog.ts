import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageNotify } from '../utils/message-notify';
import { Subject, BehaviorSubject, Observable, timer, Subscription } from 'rxjs';
import { publishReplay, refCount, scan, map, delay } from 'rxjs/operators';
import { AppsService } from './apps.service';
import { AppOperation, App } from '../app.entity';

export type IModelOperation = (data: ModelData) => ModelData;
export class ModelData {
    constructor(public jobId: number, public logs: string, public index: number, public size: number, public pollCount: number) {
    };
}
const JOB_LOGS_MAX_POLL_COUNT = 300;

@Component({
    selector: 'job-play-dialog',
    templateUrl: './job-play.dialog.html'
})
export class JobPlayDialog implements OnInit, OnDestroy {

    operation: AppOperation;
    app: App;
    hosts: Array<number>;

    jobStarted: BehaviorSubject<boolean> = new BehaviorSubject(false);
    // opAddModel     ──┬──＞ updates  ===＞ jobLogs
    opAddModel: Subject<ModelData> = new Subject();
    updates: Subject<IModelOperation> = new Subject();
    jobLogs: Observable<ModelData>;
    logsPollTimerSubscription: Subscription;

    constructor(public modal: NgbActiveModal, public svc: AppsService, private alert: MessageNotify) {
    }

    ngOnInit(): void {
        this.jobLogs = this.updates.pipe(
            scan((modelData: ModelData, op: IModelOperation) => {
                const newData = op(modelData);
                return newData;
            }, new ModelData(0,'',0,0,0)),
            publishReplay(1),
            refCount()
        );
        this.opAddModel.pipe(
            delay(100),
            map(function (newData: ModelData): IModelOperation {
                return (oldData: ModelData) => {
                    return newData;
                }
            })
        ).subscribe(this.updates);
        this.opAddModel.pipe(delay(1000)).subscribe(data => {
            if (data.index != -1 && data.pollCount < JOB_LOGS_MAX_POLL_COUNT) {
                this.svc.pollJobLogs(data.jobId, data.index).subscribe(s => {
                    if (s.code == 0) {
                        let ret = s.result;
                        this.opAddModel.next(new ModelData(data.jobId, data.logs+ret.log, ret.index, ret.size, data.pollCount+1));
                    }
                })
            }
        })
    }

    ngOnDestroy(): void {
        if (this.logsPollTimerSubscription) {
            this.logsPollTimerSubscription.unsubscribe();
        }
    }

    startJob() {
        this.svc.startJob(this.app,this.operation, this.hosts).subscribe(ret => {
            if (ret.code == 0) {
                this.jobStarted.next(true);
                let jobId = ret.result.id;
                this.svc.pollJobLogs(jobId, 0).subscribe(ret => {
                    if (ret.code == 0) {
                        this.opAddModel.next(new ModelData(jobId, ret.result.log, ret.result.index, ret.result.size, 1));
                    }
                })
            }
        });
    }
}
