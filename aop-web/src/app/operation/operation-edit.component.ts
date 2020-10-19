import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { OperationsService } from './operations.service';
import { MessageNotify } from '../utils/message-notify';
import { AppOperation, AppOperationCode } from '../app.entity';
import { AccountService } from '../account/account.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { NewCodeFileDialog } from './new-code-file.dialog';

@Component({
    selector: 'operation-edit',
    templateUrl: './operation-edit.component.html'
})
export class OperationEditComponent implements OnInit {

    public operation: AppOperation = new AppOperation();
    public editForm: FormGroup;
    public isNewAction: boolean;
    private appTypeId: number;
    private activeCode: AppOperationCode = new AppOperationCode();

    constructor(public svc: OperationsService,
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
            description: new FormControl('',[Validators.required,Validators.maxLength(128),Validators.minLength(6)]),
            command: new FormControl('',[Validators.required,Validators.maxLength(256),Validators.minLength(1)]),
            codeContent: new FormControl('', [Validators.required,Validators.maxLength(65535)])
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
                        this.command.setValue(op.command);
                        if (this.operation.codes.length > 0) {
                            this.setActiveCode(this.operation.codes[0])
                        }
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
        op.command = this.command.value;
        this.activeCode.code = this.codeContent.value;
        if (this.isNewAction) {
            op.appType = this.appType;
        }
        this.svc.saveOperation(op).subscribe(ret => {
            if (ret.code == 0) {
                if (this.isNewAction) {
                    this.svc.model.opSetModel.next(ret.result);
                }
                this.alert.success('保存脚本成功');
                this.router.navigate(['/operations']);
            }
        });
    }

    private setActiveCode(code: AppOperationCode) {
        if (this.activeCode != undefined) {
            this.activeCode.code = this.codeContent.value;
        }
        this.activeCode = code;
        this.codeContent.setValue(this.activeCode.code);
    }

    newCode() {
        let ref = this.modal.open(NewCodeFileDialog);
        ref.componentInstance.operation = this.operation;
        ref.result.then(result => {
            if (result != 'cancel') {
                this.selectCode(result);
            }
        },reason => {})
    }

    deleteCode(code: AppOperationCode) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "确认要删除吗?"
        ref.componentInstance.message = "删除文件: "+code.fileName
        ref.result.then(result => {
            if (result == "ok") {
                if (this.isNewAction) {
                    this.doDeleteCode(code);
                } else {
                    this.svc.deleteOperationCode(code).subscribe(ret => {
                        if (ret) {
                            this.doDeleteCode(code);
                        }
                    });
                }
            }
        }, resaon => {})
    }

    doDeleteCode(code: AppOperationCode) {
        let codes: Array<AppOperationCode> = [];
        for (let c of this.operation.codes) {
            if (c.fileName != code.fileName) {
                codes.push(c);
            }
        }
        this.operation.codes = codes;
        if (code.fileName == this.activeCode.fileName) {
            if (this.operation.codes.length > 0) {
                this.setActiveCode(this.operation.codes[0])
            } else {
                this.setActiveCode(new AppOperationCode());
            }
        }
    }

    isActiveCode(code: AppOperationCode): boolean {
        return this.activeCode.fileName == code.fileName;
    }

    selectCode(code: AppOperationCode) {
        this.setActiveCode(code);
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
    get command() {
        return this.editForm.get('command');
    }
    get codeContent() {
        return this.editForm.get('codeContent');
    }
    get appType() {
        return this.svc.appTypesMap[this.appTypeId];
    }
}

