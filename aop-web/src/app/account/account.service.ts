import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { LoginInfo, SignupInfo } from './account.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { map, flatMap } from 'rxjs/operators';

type ChildPermMap = Map<string, Set<string>>;

@Injectable()
export class AccountService {
  private EMETY_SET: Set<string> = new Set();
  public loginUser: Subject<string> = new BehaviorSubject<string>('');
  public loginUserPerms: Subject<Set<string>> = new BehaviorSubject<Set<string>>(this.EMETY_SET);
  public loginUserRoles: Subject<Set<string>> = new BehaviorSubject<Set<string>>(this.EMETY_SET);
  public permChilds: Subject<ChildPermMap> = new BehaviorSubject<ChildPermMap>(new Map());

  public constructor(private httpUtils: HttpUtils, private router: Router, private alert : MessageNotify) {
    const exp = localStorage.getItem('token_expires');
    const now = new Date().valueOf();
    if (exp && parseInt(exp, 10) > now) {
      const userName = localStorage.getItem('login_user');
      if (userName) {
        this.loginUser.next(userName);
        this.getPermsAndRoles(userName);
      }
    }
    this.getAllPermChilds();    
  }

  private getPermsAndRoles(userName: string) {
    this.loginUserPerms.next(this.EMETY_SET);
    this.httpGetUserPermissions(userName).subscribe(
      resp => {
        if (resp.code === 0) {
          this.loginUserPerms.next(new Set<string>(resp.result));
        }
      }
    )
    this.loginUserRoles.next(this.EMETY_SET);
    this.httpGetUserRoles(userName).subscribe(
      resp => {
        if (resp.code === 0) {
          this.loginUserRoles.next(new Set<string>(resp.result));
        }
      }
    )
  }

  private getAllPermChilds() {
    this.httpGetAllPermissionChilds().subscribe(
      resp => {
        if (resp.code == 0) {
          let map : ChildPermMap = new Map();
          for (let key of Object.keys(resp.result)) {
            map.set(key, new Set(resp.result[key]));
          }
          this.permChilds.next(map);
        }
      }
    )
  }

  public login(info: LoginInfo): void {
    this.httpLogin(info).subscribe(
      response => {
        if (response.code === 0) {
          this.loginUser.next(info.name);
          this.getPermsAndRoles(info.name);
          localStorage.setItem('token_expires',  response.result);
          localStorage.setItem('login_user', info.name);
          this.router.navigate(['/apps']);
        } else {
          this.loginUser.next('');
          this.loginUserPerms.next(this.EMETY_SET);
          this.alert.warning(response.error);
        }
      });
  }

  public signup(info: SignupInfo): void {
    this.httpSignup(info).subscribe(
      response => {
        if (response.code === 0) {
          this.loginUser.next(info.name);
          this.getPermsAndRoles(info.name);
          localStorage.setItem('token_expires',  response.result);
          localStorage.setItem('login_user', info.name);
          this.router.navigate(['/apps']);
          this.alert.success("注册成功");
        } else {
          this.loginUser.next('');
          this.loginUserPerms.next(this.EMETY_SET);
          this.alert.warning(response.error);
        }
      });
  }

  public logout(): void {
    this.loginUser.next('');
    this.loginUserPerms.next(this.EMETY_SET);
    this.loginUserRoles.next(this.EMETY_SET);
    localStorage.setItem('token_expires',  '0');
    localStorage.setItem('login_user', '');
    const exp: Date = new Date();
    exp.setTime(exp.getTime() - 24 * 60 * 60_000);
    const cookie = 'rememberMe=;path=/;expires=' + exp.toUTCString();
    document.cookie = cookie;
    const cookie1 = 'JSESSIONID=;path=/;expires=' + exp.toUTCString();
    document.cookie = cookie1;
    this.httpLogout().subscribe(() => {});
  }

  public hasRole(role: string): Observable<boolean> {
    return this.loginUserRoles.pipe(
      map(roles => roles.has(role)));
  }

  public hasPerm(perm: string): Observable<boolean> {
    return this.loginUserPerms.pipe(
      map(perms => perms.has(perm))
    )
  }

  public hasPermOrChild(perm: string): Observable<boolean> {
    return this.permChilds.pipe(
      flatMap(m => {
        let childs = m.get(perm);
        if (childs) {
          return this.loginUserPerms.pipe(
            map(perms => {
              if (perms.has(perm)) {
                return true;
              }
              for (let p of perms) {
                if (childs.has(p)) {
                  return true;
                }
              }
              return false;
          }))
        } else {
          return new BehaviorSubject(false);
        }
      }))
  }

  public perm(name: string): Observable<boolean> {
    return this.hasPerm(name).pipe(map(b => !b));
  }

  public perms(name: string): Observable<boolean> {
    return this.hasPermOrChild(name).pipe(map(b => !b));
  }

  public httpLogin(info: LoginInfo): Observable<ServiceResponse<string>> {
      const url = environment.accountApiUrl + '/api/login';
      return this.httpUtils.httpPost('用户登录', url, info);
  }

  public httpSignup(info: SignupInfo): Observable<ServiceResponse<string>> {
      const url = environment.accountApiUrl + '/api/signup';
      return this.httpUtils.httpPost('用户注册', url, info);
  }

  public httpLogout(): Observable<ServiceResponse<string>> {
    const url = environment.accountApiUrl + '/api/logout';
    return this.httpUtils.httpGet('用户登出', url);
  }

  public httpGetUserPermissions(userName: string): Observable<ServiceResponse<string[]>> {
    const url = environment.accountApiUrl + '/api/user/permissions?' + 'name=' + userName ;
    return this.httpUtils.httpGet('用户权限查询', url);
  }

  public httpGetUserRoles(userName: string): Observable<ServiceResponse<string[]>> {
    const url = environment.accountApiUrl + '/api/user/roles?' + 'name=' + userName ;
    return this.httpUtils.httpGet('用户角色查询', url);
  }

  public httpGetAllPermissionChilds(): Observable<ServiceResponse<Object>> {
    const url = environment.accountApiUrl + '/api/user/permissions/childs';
    return this.httpUtils.httpGet('子权限查询', url);
  }
}
