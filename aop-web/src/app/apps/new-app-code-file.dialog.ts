import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { FormGroup, FormControl, Validators, AbstractControl, ValidatorFn } from '@angular/forms'
import { AppOperation, AppCustomOperationCode } from '../app.entity'


@Component({
    selector: 'new-app-code-file-dialog',
    templateUrl: './new-app-code-file.dialog.html'
})
export class NewAppCodeFileDialog {

    public operations: Array<AppOperation> = []
    createForm: FormGroup

    constructor(public modal: NgbActiveModal) {
        this.createForm = new FormGroup({
            operationId: new FormControl(0, [Validators.required]),
            name: new FormControl('', [
                Validators.required,
                Validators.minLength(1),
                Validators.maxLength(32),
                this.DuplicateNameValidator()
            ]),
        })
    }

    DuplicateNameValidator(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: any } | null => {
            for (let op of this.operations) {
                for (let c of op.codes) {
                    if (control.value == c.fileName) {
                        return { 'duplicateName': { value: control.value } }
                    }
                }
            }
            return null
        }
    }

    create(event: FormDataEvent) {
        event.preventDefault()
        let name = this.createForm.get('name').value
        let c = new AppCustomOperationCode()
        if (this.operationId.value > 0) {
            c.operationId = this.operationId.value
        }
        c.fileName = name
        this.modal.close(c)
    }

    get name() {
        return this.createForm.get('name')
    }

    public get operationId(): AbstractControl {
        return this.createForm.get('operationId')
    }
}
