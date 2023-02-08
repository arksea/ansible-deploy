import { Injectable } from '@angular/core'
import { App, AppType } from '../app.entity'
import { Page } from '../app.entity'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'

@Injectable()
export class AppListService {

    searchPrefix: string = ''
    pageIndex: number = 0
    pageSize: number = 7
    appList : Page<App> = new Page()
    appTypes: AppType[] = []
    public constructor(private api: AppsService, protected alert: MessageNotify) {
        this.api.getAppTypes().subscribe(ret => {
            if (ret.code == 0) {
                this.appTypes = ret.result
                this.query()
            }
        })
    }

    public query() {
        let page = this.pageIndex + 1;
        this.api.getUserApps(page, this.pageSize, this.searchPrefix).subscribe(ret => {
            if (ret.code == 0) {
                this.appList = ret.result
            }
        })
    }

    public deleteApp(app: App) {
        this.api.deleteApp(app.id).subscribe(succeed => {
            if (succeed) {
                this.appList.items = this.appList.items.filter(it => it.id != app.id)
                this.alert.success('已删除')
            }
        })
    }
}
