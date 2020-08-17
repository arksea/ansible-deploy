import { Component } from '@angular/core';
import { Subject, Observable, BehaviorSubject } from 'rxjs';
// import { AccountService } from './account/account.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html'
})
export class AppNavbarComponent {

  loginUser: Subject<string> = new BehaviorSubject<string>('xiaohaixing');

//   constructor(private accountService: AccountService) {
//     this.loginUser = accountService.loginUser;
//   }

  hiddenLogoutMenu(): Observable<boolean> {
    return this.loginUser.pipe(map(u => u === ''))
  }

  logout() {
    // this.accountService.logout();
  }

  perm(name: string): Observable<boolean> {
    // return this.accountService.hasPerm(name).pipe(map(b => !b));
    return null;
  }

  perms(name: string): Observable<boolean> {
    // return this.accountService.hasPermOrChild(name).pipe(map(b => !b));
    return null;
  }

}
