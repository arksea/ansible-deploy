import { Injectable } from '@angular/core';
import { Router, CanActivate, CanActivateChild } from '@angular/router';

@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const exp = localStorage.getItem('token_expires');
    const now = new Date().valueOf();
    console.debug('token_expires=' + exp + ', now=' + now);
    if (exp && parseInt(exp, 10) > now) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }

  canActivateChild() : boolean {
    return this.canActivate();
  }
}
