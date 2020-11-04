import { Component } from '@angular/core'
import { FormGroup,FormControl,AbstractControl, Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { AppTypesService } from './app-types.service'
import { AppVarDefine, PortType } from '../app.entity'

@Component({
    selector: 'new-var-define-dialog',
    templateUrl: './new-var-define.dialog.html'
})
export class NewAppVarDefineDialog {

    private _appVarDefine: AppVarDefine
    public form: FormGroup
    public portTypes: Array<PortType> = []
    public portTypesMap: Map<number,PortType> = new Map()

    constructor(public modal: NgbActiveModal, public svc: AppTypesService) {
        this.svc.getPortTypes().subscribe(ret => {
            if (ret.code == 0) {
                this.portTypes = ret.result
                this.portTypes.forEach(t => this.portTypesMap[t.id]=t)
            }
        })
        this.form = new FormGroup({
            name: new FormControl('',[Validators.required, Validators.maxLength(24)]),
            formLabel: new FormControl('',[Validators.required,Validators.maxLength(64)]),
            inputAddon: new FormControl('',[Validators.maxLength(32)]),
            defaultValue: new FormControl('',[Validators.maxLength(256)]),
            portType: new FormControl(-1),
        })
    }

    set appVarDefine(def: AppVarDefine) {
        this._appVarDefine = def
        this.name.setValue(def.name)
        this.formLabel.setValue(def.formLabel)
        this.inputAddon.setValue(def.inputAddon)
        this.defaultValue.setValue(def.defaultValue)
        if (def.portType) {
            this.portType.setValue(def.portType.id)
        } else {
            this.portType.setValue(-1)
        }
    }

    save() {
        event.preventDefault()
        let d = this._appVarDefine
        d.name = this.name.value
        d.formLabel = this.formLabel.value
        d.inputAddon = this.inputAddon.value
        d.defaultValue = this.defaultValue.value
        if (this.portType.value != -1) {
            d.portType = this.portTypesMap[this.portType.value]
        }
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
    get portType(): AbstractControl {
        return this.form.get('portType')
    }
}
