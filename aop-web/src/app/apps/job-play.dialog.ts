import { Component, OnDestroy } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageNotify } from '../utils/message-notify';
import { Subject, BehaviorSubject, Observable, timer, Subscription } from 'rxjs';
import { publishReplay, refCount, scan, map } from 'rxjs/operators';
import { AppsService, PollLogsResult } from './apps.service';
import { Host, Version, App } from '../app.entity';
import { FormGroup, FormArray, FormControl } from '@angular/forms';

export type IModelOperation = (data: ModelData) => ModelData;
export class ModelData {
    constructor(public logs: string, public index: number) {
    };
}

@Component({
    selector: 'job-play-dialog',
    templateUrl: './job-play.dialog.html'
})
export class JobPlayDialog implements OnDestroy {


    jobStarted: BehaviorSubject<boolean> = new BehaviorSubject(false);
    // opAddModel     ──┬──＞ updates  ===＞ jobLogs
    opAddModel: Subject<PollLogsResult> = new Subject();
    updates: Subject<IModelOperation> = new Subject();
    jobLogs: Observable<ModelData>;
    logsPollTimerSubscription: Subscription;

    constructor(public modal: NgbActiveModal, public svc: AppsService, private alert: MessageNotify) {
        this.jobLogs = this.updates.pipe(
            scan((modelData: ModelData, op: IModelOperation) => {
                const newData = op(modelData);
                return newData;
            }, new ModelData('',0)),
            publishReplay(1),
            refCount()
        );
        this.opAddModel.pipe(
            map(function (ret: PollLogsResult): IModelOperation {
                return (oldData: ModelData) => {
                    return new ModelData(oldData.logs + ret.log, ret.index);
                }
            })
        ).subscribe(this.updates);
    }

    ngOnDestroy(): void {
        this.logsPollTimerSubscription.unsubscribe();
    }

    startJob() {
         this.svc.startJob().subscribe(ret => {
             if (ret.code == 0) {
                this.jobStarted.next(true);
                let jobId = ret.result.id;
                let numbers = timer(1000, 1000);
                let index = 0;
                this.logsPollTimerSubscription = numbers.subscribe(x => {
                        this.svc.pollJobLogs(jobId, index).subscribe(ret => {
                            if (ret.result.log != '') {
                                index = ret.result.index;
                                this.opAddModel.next(ret.result);
                            }
                            if (ret.result.index == -1) {
                                this.logsPollTimerSubscription.unsubscribe();
                            }
                         })
                    });
             }
         });
    }
}
