import { Component, OnInit } from '@angular/core';
import { GroupsService } from './groups.service';
import { AccountService } from '../account/account.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HostsService } from '../hosts/hosts.service';
import { MessageNotify } from '../utils/message-notify';
import { Host } from '../app.entity';

@Component({
    selector: 'group-hosts',
    templateUrl: './group-hosts.component.html'
})
export class GroupHostsComponent implements OnInit {

    public model: any;

    searchHost = (text: Observable<string>) => this.svc.search(text, '没有未分组的主机', this.hostSvc.hostList.pipe(map(list => {
        let names: string[] = [];
        for (let i of list) {
            names.push(i.privateIp)
        }
        return names;
    })));

    constructor(public svc: GroupsService,
                public account: AccountService,
                public hostSvc: HostsService,
                private alert: MessageNotify) {
        this.hostSvc.queryHostsNotInGroup();
    }

    ngOnInit() { }

    addHost(hostIp: string) {
        this.hostSvc.getHostByIp(hostIp).subscribe(h => {
            if (h == null) {
                this.alert.warning('未找到主机:'+hostIp);
            } else {
                this.svc.addHost(h).subscribe(succeed => {
                    if (succeed)this.alert.info('添加主机成功');
                })
            }
        })
    }

    removeHost(host: Host) {
        return this.svc.removeHost(host).subscribe(succeed => {
            if (succeed) {
                this.alert.info('移除主机成功');
            }
        });
    }
}
