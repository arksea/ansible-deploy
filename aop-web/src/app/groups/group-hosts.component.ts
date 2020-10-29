import { Component, OnInit } from '@angular/core'
import { GroupsService } from './groups.service'
import { AccountService } from '../account/account.service'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators'
import { MessageNotify } from '../utils/message-notify'
import { Host, AppGroup } from '../app.entity'

@Component({
    selector: 'group-hosts',
    templateUrl: './group-hosts.component.html'
})
export class GroupHostsComponent implements OnInit {

    public model: any
    public group: AppGroup
    private notInGroupHosts: Array<Host> = []
    
    searchHost = (text: Observable<string>) =>  this.svc.search(text, this.svc.getHostsNotInGroup().pipe(
        map(ret => { 
            this.notInGroupHosts=ret.result
            return ret.code == 0 ? ret.result.map(it => it.privateIp) : [] 
        })
    ))

    constructor(public svc: GroupsService,
                public account: AccountService,
                private alert: MessageNotify) {
        this.svc.getHostsNotInGroup().subscribe(ret => {
            if (ret.code == 0) {
                this.notInGroupHosts = ret.result
            }
        })
        this.svc.getCurrentGroup().subscribe(ret => {
            if (ret.code == 0) {
                this.group = ret.result
            }
        })
    }

    ngOnInit() { }

    addHost(hostIp: string) {
        for (let h of this.notInGroupHosts) {
            if (h.privateIp == hostIp) {
                this.svc.addHost(h).subscribe(ret => {
                    if (ret.code == 0) {
                        this.group.hosts.push(h)
                        this.alert.success('添加主机成功')
                    }
                })
                return
            }
        }
        this.alert.warning('未找到主机:'+hostIp)
    }

    removeHost(host: Host) {
        return this.svc.removeHost(host).subscribe(ret => {
            if (ret.code == 0) {
                this.group.hosts = this.group.hosts.filter(it => it.id != host.id)
                this.alert.success('移除主机成功')
            }
        })
    }
}
