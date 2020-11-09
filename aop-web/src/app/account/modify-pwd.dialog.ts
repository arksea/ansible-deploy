import { Component } from '@angular/core'
import { FormDataEvent } from '@angular/forms/esm2015'
import { FormGroup,FormControl,Validators } from '@angular/forms'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'
import { MessageNotify } from '../utils/message-notify'
import { AccountService } from './account.service'

@Component({
    selector: 'modify-pwd',
    templateUrl: './modify-pwd.dialog.html'
})
export class ModifyPwdDialog {
    form: FormGroup

    constructor(public modal: NgbActiveModal, private svc: AccountService, private alert: MessageNotify) {
        this.form = new FormGroup({
            password: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
            password2:new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)])
        })
    }

    save(event: FormDataEvent) {
        event.preventDefault()
        let p1 = this.form.get('password').value
        let p2 = this.form.get('password2').value
        if (p1 == p2) {
          this.svc.modifyPassword(p1).subscribe(ret => {
              if (ret.code == 0) {
                this.alert.success('密码已修改')
                this.modal.close('ok')
              }
          })
        } else {
          this.alert.warning('两次输入的密码不同')
        }
    }
}
