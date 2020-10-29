import { Component, OnInit } from '@angular/core';
import { SignupInfo } from './account.entity';
import { AccountService } from './account.service';
import { MessageNotify } from "../utils/message-notify";
import { FormDataEvent } from "@angular/forms/esm2015";
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-sign-up',
  styleUrls: ['./account.css'],
  templateUrl: './sign-up.component.html'
})
export class SignUpComponent implements OnInit {

  constructor(private accountService: AccountService, private alert: MessageNotify, ) { }

  ngOnInit(): void { }
  signUpForm: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(24)]),
    email:    new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
    password: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
    password2:new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)])
  });

  signup(event: FormDataEvent) {
    event.preventDefault();
    if (this.signUpForm.get('password').value === this.signUpForm.get('password2').value) {
      const body = new SignupInfo();
      body.name = this.signUpForm.get('username').value;
      body.email = this.signUpForm.get('email').value;
      body.password = this.signUpForm.get('password').value;
      this.accountService.signup(body);
    } else {
      this.alert.warning('两次输入的密码不同');
    }
  }
}
