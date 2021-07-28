import { Component } from '@angular/core'
import { FormGroup,FormControl,AbstractControl, Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { OperationVarDefine } from '../app.entity'

@Component({
    selector: 'new-operation-var-define-dialog',
    templateUrl: './new-operation-var-define.dialog.html'
})
export class NewOperationVarDefineDialog {

    private _operationVarDefine: OperationVarDefine
    public form: FormGroup

    constructor(public modal: NgbActiveModal) {
        this.form = new FormGroup({
            name: new FormControl('',[Validators.required, Validators.maxLength(24)]),
            formLabel: new FormControl('',[Validators.required,Validators.maxLength(64)]),
            inputAddon: new FormControl('',[Validators.maxLength(32)]),
            defaultValue: new FormControl('',[Validators.maxLength(256)])
        })
    }

    set versionVarDefine(def: OperationVarDefine) {
        this._operationVarDefine = def
        this.name.setValue(def.name)
        this.formLabel.setValue(def.formLabel)
        this.inputAddon.setValue(def.inputAddon)
        this.defaultValue.setValue(def.defaultValue)
    }

    save() {
        event.preventDefault()
        let d = this._operationVarDefine
        d.name = this.name.value
        d.formLabel = this.formLabel.value
        d.inputAddon = this.inputAddon.value
        d.defaultValue = this.defaultValue.value
        this.modal.close('ok')
    }

    get name(): AbstractControl {
        return this.form.get('name')
    }
    get formLabel(): AbstractControl {
        return this.form.get('formLabel')
    }
    get inputAddon(): AbstractControl {
        return this.form.get('inputAddon')
    }
    get defaultValue(): AbstractControl {
        return this.form.get('defaultValue')
    }
}
