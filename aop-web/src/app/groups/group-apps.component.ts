import { Component, OnInit } from '@angular/core'
import { GroupsService } from './groups.service'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from '../account/account.service'
import { map } from 'rxjs/operators'
import { Observable } from 'rxjs'
import { App, AppGroup } from '../app.entity'

@Component({
    selector: 'group-apps',
    templateUrl: './group-apps.component.html'
})
export class GroupAppsComponent implements OnInit {
    public model: any

    public notInGroupApp: App[] = []
    public group: AppGroup

    search = (text: Observable<string>) =>  this.svc.search(text, this.svc.getAppsNotInGroup().pipe(
        map(ret => { 
            this.notInGroupApp = ret.result
            return ret.code == 0 ? this.notInGroupApp.map(it => it.apptag) : [] 
        })
    ))

    constructor(public svc: GroupsService,
                public account: AccountService,
                private alert: MessageNotify) {
        this.svc.getCurrentGroup().subscribe(ret => {
            if (ret.code == 0) {
                this.group = ret.result
            }
        })
    }

    ngOnInit() {}

    addApp(apptag: string) {
        for (let a of this.notInGroupApp) {
            if (a.apptag == apptag) {
                this.svc.addApp(a).subscribe(ret => {
                    if (ret.code == 0) {
                        this.group.apps.push(a)
                        this.alert.success('添加应用成功')
                    }
                })
                return
            }
        }
        this.alert.warning('未找到应用:'+apptag)
    }

    removeApp(app: App) {
        return this.svc.removeApp(app).subscribe(ret => {
            if (ret.code == 0) {
                this.group.apps = this.group.apps.filter(it => it.id != app.id)
                this.alert.success('移除应用成功')
            }
        })
    }
}
