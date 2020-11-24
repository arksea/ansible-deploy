import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { AccountService } from './account.service';
import { FormGroup,FormControl,Validators } from '@angular/forms';

@Component({
  selector: 'account-login',
  styleUrls: ['./account.css'],
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  public openRegister: boolean = true;
  constructor(private accountService: AccountService) {
  }

  loginForm: FormGroup = new FormGroup({
    username: new FormControl('',[Validators.required]),
    password: new FormControl('',[Validators.required])
  });

  ngOnInit(): void {
    this.accountService.getOpenRegister().subscribe(ret => {
        if (ret.code == 0) {
          this.openRegister = ret.result
        }
    })
  }

  login(event: FormDataEvent) {
    event.preventDefault();
    let n = this.loginForm.get('username').value;
    let p = this.loginForm.get('password').value;
    this.accountService.login({name: n, password: p});
  }
}
