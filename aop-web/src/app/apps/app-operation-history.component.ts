import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { FormGroup, FormControl } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AppsService } from './apps.service'
import { MessageNotify } from '../utils/message-notify'
import { App, OperationJobInfo } from '../app.entity'
import { AccountService } from '../account/account.service'


@Component({
    selector: 'app-operation-history',
    templateUrl: './app-operation-history.component.html'
})
export class AppOperationHistoryComponent implements OnInit {
    app: App = new App()
    public history: OperationJobInfo[] = []

    constructor(private svc: AppsService,
                public account: AccountService,
                private route: ActivatedRoute,
                private router: Router,
                private modal: NgbModal,
                private alert: MessageNotify) {
        let appId = Number(this.route.snapshot.paramMap.get('id'))
        this.svc.getAppById(appId).subscribe(ret => {
            if (ret.code == 0) {
                this.app = ret.result
                this.svc.getAppOperationJobHistory(this.app.id).subscribe(ret => {
                    if (ret.code == 0) {
                        this.history = ret.result
                    }
                })
            } else {
                this.alert.warning("应用不存在或无权限(id="+appId+")")
                this.router.navigate(["/apps"])
            }
        })
    }

    ngOnInit() {}


}
