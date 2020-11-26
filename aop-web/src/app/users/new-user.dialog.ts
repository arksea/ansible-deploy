import { Component, OnInit } from '@angular/core'
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SignupInfo } from '../account/account.entity'
import { AccountService } from '../account/account.service'
import { MessageNotify } from "../utils/message-notify"
import { FormDataEvent } from "@angular/forms/esm2015"
import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms'
import { User } from './users.entity';

@Component({
    selector: 'new-user',
    templateUrl: './new-user.dialog.html'
})
export class NewUserDialog implements OnInit {

    constructor(public modal: NgbActiveModal, private accountService: AccountService, private alert: MessageNotify, ) { }

    ngOnInit(): void { }
    public createForm: FormGroup = new FormGroup({
        username: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(24)]),
        email: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
        password: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
        password2: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)])
    })

    create(event: FormDataEvent) {
        event.preventDefault()
        if (this.pwd.value === this.pwd2.value) {
            const body = new SignupInfo()
            body.name = this.name.value
            body.email = this.email.value
            body.password = this.pwd.value
            this.accountService.createUser(body).subscribe(ret => {
                if (ret.code == 0) {
                    this.modal.close(ret.result)
                }
            })
        } else {
            this.alert.warning('两次输入的密码不同')
        }
    }

    get name(): AbstractControl {
        return this.createForm.get('username')
    }

    get email(): AbstractControl {
        return this.createForm.get('email')
    }

    get pwd(): AbstractControl {
        return this.createForm.get('password')
    }

    get pwd2(): AbstractControl {
        return this.createForm.get('password2')
    }
}
