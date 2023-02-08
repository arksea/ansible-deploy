import { Component, OnInit } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { AppsService } from './apps.service'
import { FormGroup, FormControl, Validators, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms'
import { AppVariable } from '../app.entity'

@Component({
    selector: 'port-select-dialog',
    templateUrl: './port-select.dialog.html'
})
export class PortSelectDialog implements OnInit {

    variable: AppVariable
    public form: FormGroup = new FormGroup({
        port: new FormControl('', [Validators.required], this.portUseableValidator())
    })

    constructor(public modal: NgbActiveModal, public svc: AppsService) {

    }

    portUseableValidator(): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            return this.svc.getPortByValue(ctrl.value).pipe(
                map(ret => {
                    if (ret.code == 0) {
                        let ports = ret.result
                        if (ports.length == 0) {
                            return {'portNotManaged':true}
                        } else {
                            if (!ports[0].enabled) {
                                return {'portUnabled':true}
                            }
                            if (ports[0].appId > 0) {
                                return {'portUsed':true}
                            }
                        }
                    }
                }),
                catchError(() => of(null))
            );
        }
    }

    ngOnInit(): void {
    }

    public submit(event: FormDataEvent) {
        event.preventDefault()
        this.variable.value = this.port.value
        this.modal.close('ok')
    }

    get port(): AbstractControl {
        return this.form.get('port')
    }

}
