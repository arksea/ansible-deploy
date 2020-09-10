import { Component, OnInit } from '@angular/core';
import { GroupsService } from './groups.service';
import { AccountService } from '../account/account.service';
import { Observable, of } from 'rxjs';
import { map,flatMap,first } from 'rxjs/operators';
import { HostsService } from '../hosts/hosts.service';
import { MessageNotify } from '../utils/message-notify';

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
        this.hostSvc.getHostsNotInGroup();
    }

    ngOnInit() { }

    addHost(hostIp: string) {
        this.hostSvc.hostList.pipe(first(),flatMap(list => {
            for (let h of list) {
                if (h.privateIp == hostIp) {
                    return this.svc.addHost(h)
                }
            }
            this.alert.warning('未找到主机:'+hostIp);
            return of(false);
        })).subscribe(h => {
            if (h) {
                this.alert.info('添加主机成功');
            }
        });
    }
}
