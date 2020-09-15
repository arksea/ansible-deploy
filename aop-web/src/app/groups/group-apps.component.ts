import { Component, OnInit } from '@angular/core';
import { GroupsService } from './groups.service';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { App } from '../app.entity';

@Component({
    selector: 'group-apps',
    templateUrl: './group-apps.component.html'
})
export class GroupAppsComponent implements OnInit {
    public model: any;

    public notInGroupApp: App[] = [];

    search = (text: Observable<string>) => 
        this.svc.search(text, '没有未分组的应用', this.svc.queryNotInGroupApps().pipe(map(list => {
            this.notInGroupApp = list;
            let names: string[] = [];
            for (let i of list) {
                names.push(i.apptag)
            }
            return names;
        })));

    constructor(public svc: GroupsService,
                public account: AccountService,
                private alert: MessageNotify) {
    }

    ngOnInit() {}

    addApp(apptag: string) {
        for (let a of this.notInGroupApp) {
            if (a.apptag = apptag) {
                this.svc.addApp(a).subscribe(succeed => {
                    if (succeed)this.alert.info('添加应用成功');
                })
                return;
            }
        }
        this.alert.warning('未找到应用:'+apptag);
    }

    removeApp(app: App) {
        return this.svc.removeApp(app).subscribe(succeed => {
            if (succeed) {
                this.alert.info('移除应用成功');
            }
        });
    }
}
