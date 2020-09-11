import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AppsService } from '../apps/apps.service';
import { App } from '../app.entity';

@Component({
    selector: 'group-apps',
    templateUrl: './group-apps.component.html'
})
export class GroupAppsComponent implements OnInit {
    public model: any;

    search = (text: Observable<string>) => this.svc.search(text, '没有未分组的应用', this.appsSvc.appList.pipe(map(list => {
        let names: string[] = [];
        for (let i of list) {
            names.push(i.apptag)
        }
        return names;
    })));
    constructor(public svc: GroupsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private appsSvc: AppsService,
                private router: Router,
                private alert: MessageNotify,
                private modal: NgbModal) {
        appsSvc.queryNotInGroupApps();
    }


    ngOnInit() {}

    addApp(apptag: string) {
        this.appsSvc.getAppByApptag(apptag).subscribe(a => {
            if (a == null) {
                this.alert.warning('未找到应用:'+apptag);
            } else {
                this.svc.addApp(a).subscribe(succeed => {
                    if (succeed)this.alert.info('添加应用成功');
                })
            }
        })
    }

    removeApp(app: App) {
        return this.svc.removeApp(app).subscribe(succeed => {
            if (succeed) {
                this.alert.info('移除应用成功');
            }
        });
    }
}
