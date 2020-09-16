import { Component } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageNotify } from '../utils/message-notify';
import { Subject, BehaviorSubject } from 'rxjs';
import { AppsService } from './apps.service';
import { Host, Version, App } from '../app.entity';
import { FormGroup, FormArray, FormControl } from '@angular/forms';

@Component({
    selector: 'add-host-dialog',
    templateUrl: './add-host.dialog.html'
})
export class AddHostDialog {

    app: App;
    version: Version;
    hosts: Host[];
    public formArray: FormArray = new FormArray([]);
    public formGroup: FormGroup = new FormGroup({hosts: this.formArray});

    public setParams(app: App, version: Version, hosts: Host[]) {
        this.version = version;
        this.app = app;
        this.hosts = hosts.filter((h,i,array)=> {
            for (let v of app.versions) {
                for (let t of v.targetHosts) { //过滤应用在所有版本中已添加的主机
                    if (t.id == h.id) {
                        return false;
                    }
                }
            }
            return true;
        });
        this.hosts.forEach(h => this.formArray.push(new FormControl({value:false,disabled:!h.enabled})));
    }

    constructor(public modal: NgbActiveModal, public svc: AppsService, private alert: MessageNotify) {
    }

    save() {
        let idList: number[] = [];
        let addHosts: Host[] = [];
        for (let i in this.hosts) {
            if (this.formArray.value[i]) {
                let h = this.hosts[i];
                idList.push(h.id);
                addHosts.push(h);
            }
        }
        if (this.app.id) {
            this.svc.addHostsToVersion(this.version.id, idList).subscribe(error => {
                if (!error) {
                    addHosts.forEach(h => this.version.targetHosts.push(h));
                    this.modal.close('ok');
                    this.alert.info("新增主机成功");
                }
            })
        } else { //未保存的新建应用
            addHosts.forEach(h => this.version.targetHosts.push(h));
            this.modal.close('ok');
        }
    }
}
