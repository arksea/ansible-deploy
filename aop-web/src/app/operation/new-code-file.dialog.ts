import { Component } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup,FormControl,Validators,AbstractControl,ValidatorFn } from '@angular/forms';
import { AppOperation, AppOperationCode } from '../app.entity';


@Component({
    selector: 'new-code-file-dialog',
    templateUrl: './new-code-file.dialog.html'
})
export class NewCodeFileDialog {

    public operation: AppOperation = new AppOperation();
    createForm: FormGroup;

    constructor(public modal: NgbActiveModal) {
        this.createForm = new FormGroup({
            name: new FormControl('',[
                Validators.required, 
                Validators.minLength(1), 
                Validators.maxLength(32), 
                this.DuplicateNameValidator()
            ]),
        });
    }

    DuplicateNameValidator(): ValidatorFn {
        return (control: AbstractControl): {[key: string]: any} | null => {
          for (let c of this.operation.codes) {
              if (control.value == c.fileName) {
                  return {'duplicateName': {value: control.value}};
              }
          }
          return null;
        };
    }

    create(event: FormDataEvent) {
        event.preventDefault();
        let name = this.createForm.get('name').value;
        let c = new AppOperationCode();
        if (this.operation.id > 0) {
            c.operationId = this.operation.id;
        }
        c.fileName = name;
        this.operation.codes.push(c);
        this.modal.close(c);
    }

    get name() {
        return this.createForm.get('name');
    }
}
