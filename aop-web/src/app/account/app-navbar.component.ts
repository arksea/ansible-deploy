import { Component } from '@angular/core'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { AccountService } from './account.service'
import { ModifyPwdDialog } from './modify-pwd.dialog'

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html'
})
export class AppNavbarComponent {

  loginUser: string

  constructor(private accountService: AccountService, private modal: NgbModal) {
    this.loginUser = accountService.loginUser
  }

  hiddenLogoutMenu(): boolean {
    return this.loginUser === ''
  }

  logout() {
    this.accountService.logout()
  }

  perm(name: string): boolean {
    return this.accountService.perm(name)
  }

  perms(name: string): boolean {
    return this.accountService.perms(name)
  }

  modifyPwd() {
    this.modal.open(ModifyPwdDialog)
  }

}
