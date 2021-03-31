import { Component, OnInit } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { OperationsService } from './operations.service'
import { MessageNotify } from '../utils/message-notify'
import { AppOperation, AppOperationCode, OperationVarDefine } from '../app.entity'
import { AccountService } from '../account/account.service'
import { Router, ActivatedRoute, ParamMap } from '@angular/router'
import { ConfirmDialog } from '../utils/confirm.dialog'
import { NewCodeFileDialog } from './new-code-file.dialog'
import { AppTypesService } from '../app-types/app-types.service'
import { NewOperationVarDefineDialog } from './new-operation-var-define.dialog'

@Component({
    selector: 'operation-edit',
    templateUrl: './operation-edit.component.html'
})
export class OperationEditComponent implements OnInit {

    public operation: AppOperation = new AppOperation()
    public editForm: FormGroup
    public isNewAction: boolean
    public defineModified: boolean = false
    public activeCode: AppOperationCode = new AppOperationCode()

    constructor(public svc: OperationsService,
                private appTypesSvc: AppTypesService,
                private account: AccountService,
                protected alert: MessageNotify,
                protected modal: NgbModal,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        let params: ParamMap =  this.route.snapshot.paramMap
        let idStr = params.get('id')
        this.editForm = new FormGroup({
            name: new FormControl({value:'',disabled:this.readonly()},[Validators.required,Validators.maxLength(32),Validators.minLength(2)]),
            description: new FormControl({value:'',disabled:this.readonly()},[Validators.required,Validators.maxLength(128),Validators.minLength(1)]),
            command: new FormControl({value:'',disabled:this.readonly()},[Validators.required,Validators.maxLength(256),Validators.minLength(1)]),
            opType: new FormControl({value:'COMMON',disabled:this.readonly()},[Validators.required]),
            codeContent: new FormControl({value:'',disabled:this.readonly()}, [Validators.maxLength(65535)])
        })
        if (idStr == 'new') {
            this.isNewAction = true
            this.operation = new AppOperation()
            let appTypeId = Number(params.get('appTypeId'))
            this.appTypesSvc.getAppType(appTypeId).subscribe(ret => {
                if (ret.code == 0) {
                    this.operation.appType = ret.result
                }
            })
        } else {
            this.isNewAction = false
            let opId = Number(idStr)
            this.svc.getOperationById(opId).subscribe(ret => {
                    if (ret.code == 0) {
                        this.operation = ret.result
                        this.name.setValue(this.operation.name)
                        this.desc.setValue(this.operation.description)
                        this.command.setValue(this.operation.command)
                        this.opType.setValue(this.operation.type)
                        if (this.operation.codes.length > 0) {
                            this.setActiveCode(this.operation.codes[0])
                        }
                    } else {
                        this.alert.warning('指定操作不存在：'+opId)
                        this.router.navigate(['/app-types',this.operation.appType.id,'operations'])
                    }
                }
            )
        }
    }

    save() {
        let op = this.operation
        op.name = this.name.value
        op.description = this.desc.value
        op.command = this.command.value
        op.type = this.opType.value
        this.activeCode.code = this.codeContent.value
        this.svc.saveOperation(op).subscribe(ret => {
            if (ret.code == 0) {
                this.alert.success('保存脚本成功')
                this.navToOperations()
            }
        })
    }

    private navToOperations() {
        this.router.navigate(['/app-types',this.operation.appType.id,'operations'])
    }

    private setActiveCode(code: AppOperationCode) {
        if (this.activeCode != undefined) {
            this.activeCode.code = this.codeContent.value
        }
        this.activeCode = code
        this.codeContent.setValue(this.activeCode.code)
    }

    newCode() {
        let ref = this.modal.open(NewCodeFileDialog)
        ref.componentInstance.operation = this.operation
        ref.result.then(result => {
            if (result != 'cancel') {
                let code = result as AppOperationCode
                this.selectCode(code)
            }
        },reason => {})
    }

    deleteCode(code: AppOperationCode) {
        let ref = this.modal.open(ConfirmDialog)
        ref.componentInstance.title = "确认要删除吗?"
        ref.componentInstance.message = "删除文件: "+code.fileName
        ref.result.then(result => {
            if (result == "ok") {
                if (this.isNewAction || code.id == undefined) {
                    this.doDeleteCode(code);
                } else {
                    this.svc.deleteOperationCode(code).subscribe(ret => {
                        if (ret) {
                            this.doDeleteCode(code);
                        }
                    })
                }
            }
        }, resaon => {})
    }

    doDeleteCode(code: AppOperationCode) {
        let codes: Array<AppOperationCode> = []
        for (let c of this.operation.codes) {
            if (c.fileName != code.fileName) {
                codes.push(c)
            }
        }
        this.operation.codes = codes
        if (code.fileName == this.activeCode.fileName) {
            if (this.operation.codes.length > 0) {
                this.setActiveCode(this.operation.codes[0])
            } else {
                this.setActiveCode(new AppOperationCode())
            }
        }
    }

    newVarDefine() {
        let ref = this.modal.open(NewOperationVarDefineDialog)
        let def = new OperationVarDefine()
        def.operationId = this.operation.id
        ref.componentInstance.versionVarDefine = def
        ref.result.then(ret => {
            if (ret == 'ok') {
                this.operation.varDefines.push(def)
                this.defineModified = true
            }
        }, reason => {})
    }

    editVarDefine(def: OperationVarDefine) {
        let ref = this.modal.open(NewOperationVarDefineDialog)
        ref.componentInstance.versionVarDefine = def
        ref.result.then(ret => {
            if (ret == 'ok') {
                this.defineModified = true
            }
        }, reason => {})
    }

    delVarDefine(def: OperationVarDefine) {
        let list = []
        for (let d of this.operation.varDefines) {
            if (d.name != def.name) {
                list.push(d)
            }
        }
        this.operation.varDefines = list
        this.defineModified = true
    }

    isActiveCode(code: AppOperationCode): boolean {
        return this.activeCode.fileName == code.fileName
    }

    selectCode(code: AppOperationCode) {
        this.setActiveCode(code)
    }

    readonly(): boolean {
        return this.account.perm('操作管理:修改')
    }

    public cancel() {
        this.navToOperations()
    }

    get name() {
        return this.editForm.get('name')
    }
    get desc() {
        return this.editForm.get('description')
    }
    get command() {
        return this.editForm.get('command')
    }
    get opType() {
        return this.editForm.get('opType')
    }
    get codeContent() {
        return this.editForm.get('codeContent')
    }
}

