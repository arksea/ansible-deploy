import { Component } from '@angular/core'
import { AccountService } from './account.service'

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html'
})
export class AppNavbarComponent {

  loginUser: string

  constructor(private accountService: AccountService) {
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

}
