import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { ActivatedRoute, ParamMap } from '@angular/router'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from '../account/account.service'
import { AppType, AppOperation } from '../app.entity'
import { AppTypesService } from '../app-types/app-types.service'

@Component({
    selector: 'operation-list',
    templateUrl: './operation-list.component.html'
})
export class OperationListComponent implements OnInit {

    appType: AppType = new AppType()
    operationList: Array<AppOperation> = []

    constructor(private modal: NgbModal, 
                private svc: AppTypesService,
                public account: AccountService,
                private alert: MessageNotify,
                private route: ActivatedRoute) {
        let params: ParamMap =  this.route.snapshot.paramMap
        let idStr = params.get('id')
        let appTypeId = Number(idStr)
        this.svc.getAppType(appTypeId).subscribe(ret => {
            if (ret.code == 0) {
                this.appType = ret.result
            }
        })
        this.svc.getOperations(appTypeId).subscribe(ret => {
            if (ret.code == 0) {
                this.operationList = ret.result
            }
        })
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    ngOnInit(): void {
    }

    onDeleteOperationBtnClick(operation: AppOperation) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "删除应用操作: " + operation.name;
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteOperation(operation).subscribe(ret => {
                    if (ret.code == 0) {
                        this.alert.success('删除成功')
                    }
                });
            }
        }, resaon => { })
    }

}