import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, AbstractControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { OperationsService } from './operations.service';
import { MessageNotify } from '../utils/message-notify';
import { AppOperation, AppType } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { HostsService } from '../hosts/hosts.service';
import { Version } from '../app.entity';
import { ConfirmDialog } from '../utils/confirm.dialog';


@Component({
    selector: 'operation-edit',
    templateUrl: './operation-edit.component.html'
})
export class OperationEditComponent implements OnInit {

    public operation: AppOperation;
    public editForm: FormGroup;
    public isNewAction: boolean;
    private appTypeId: number;
    constructor(public svc: OperationsService,
                private hostSvc: HostsService,
                public account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        let params: ParamMap =  this.route.snapshot.paramMap;
        let idStr = params.get('id');
        this.appTypeId = Number(params.get('appTypeId'));
        this.editForm = new FormGroup({
            name: new FormControl('',[Validators.required,Validators.maxLength(16),Validators.minLength(2)]),
            description: new FormControl('',[Validators.required,Validators.maxLength(512),Validators.minLength(6)]),
            playbook: new FormControl('', [Validators.required,Validators.maxLength(65535)])
        });
        if (idStr == 'new') {
            this.isNewAction = true;
            this.operation = new AppOperation();
        } else {
            this.isNewAction = false;
            let opId = Number(idStr);
            this.svc.getOperationById(opId).subscribe(
                op => {
                    if (op) {
                        this.operation = op;
                        this.name.setValue(op.name);
                        this.desc.setValue(op.description);
                        this.playbook.setValue(op.playbook);
                    } else {
                        this.alert.warning('指定操作不存在：'+opId);
                        this.router.navigate(['/operations'])
                    }
                }
            )
        }
    }

    save() {
        let op = this.operation;
        op.name = this.name.value;
        op.description = this.desc.value;
        op.playbook = this.playbook.value;
        if (this.isNewAction) {
            op.appType = this.appType;
        }
        this.svc.saveOperation(op).subscribe(ret => {
            if (ret.code == 0) {
                if (this.isNewAction) {
                    op.id = ret.result;
                    this.svc.model.opSetModel.next(op);
                }
                this.alert.success('保存脚本成功');
                this.router.navigate(['/operations']);
            }
        });
    }

    public cancel() {
        this.router.navigate(['/operations'])
    }

    get name() {
        return this.editForm.get('name');
    }
    get desc() {
        return this.editForm.get('description');
    }
    get playbook() {
        return this.editForm.get('playbook');
    }
    get appType() {
        return this.svc.appTypesMap[this.appTypeId];
    }
}
