import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { Router } from '@angular/router'
import { OperationsService } from './operations.service'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from '../account/account.service'
import { AppType, AppOperation } from '../app.entity'

@Component({
    selector: 'operation-list',
    templateUrl: './operation-list.component.html'
})
export class OperationListComponent implements OnInit {

    constructor(private modal: NgbModal, public svc: OperationsService,
        public account: AccountService,
        private alert: MessageNotify,
        private router: Router) {
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    ngOnInit(): void {
    }

    onSelectAppTypeBtnClick(appType: AppType) {
        this.svc.setSelectedAppType(appType);
    }

    onNewOperationBtnClick() {
        this.router.navigate(['/operations/new/edit', this.svc.appType.id])
    }

    onEditOperationBtnClick(operation: AppOperation) {
        this.router.navigate(['/operations/' + operation.id + '/edit', this.svc.appType.id])
    }

    onDeleteOperationBtnClick(operation: AppOperation) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "删除应用操作: " + operation.name;
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteOperation(operation).subscribe(succeed => {
                    if (succeed) {
                        this.alert.success('删除成功');
                    }
                });
            }
        }, resaon => { });
    }

}