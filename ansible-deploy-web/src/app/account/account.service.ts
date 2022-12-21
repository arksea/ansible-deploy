import { Injectable } from '@angular/core'
import { Router } from '@angular/router'
import { Observable } from 'rxjs'
import { LoginInfo, SignupInfo } from './account.entity'
import { ServiceResponse } from '../utils/http-utils'
import { HttpUtils } from '../utils/http-utils'
import { MessageNotify } from "../utils/message-notify"
import { environment } from '../../environments/environment'
import { User } from '../users/users.entity'
import { HttpHeaders } from '@angular/common/http'

type ChildPermMap = Map<string, Set<string>>

@Injectable()
export class AccountService {

    public loginUser: string = ''

    private EMETY_SET: Set<string> = new Set()
    private loginUserPerms: Set<string> = this.EMETY_SET
    private loginUserRoles: Set<string> = this.EMETY_SET
    private permChilds: ChildPermMap = new Map()

    public constructor(private httpUtils: HttpUtils, private router: Router, private alert: MessageNotify) {
        const exp = localStorage.getItem('token_expires')
        const now = new Date().valueOf()
        if (exp && parseInt(exp, 10) > now) {
            const userName = localStorage.getItem('login_user')
            if (userName) {
                this.loginUser = userName
                this.getPermsAndRoles()
            }
        }
        this.getAllPermChilds();
    }

    private getPermsAndRoles() {
        this.loginUserPerms = this.EMETY_SET
        this.httpGetUserPermissions().subscribe(resp => {
            if (resp.code === 0) {
                this.loginUserPerms = new Set<string>(resp.result)
            }
        })
        this.loginUserRoles = this.EMETY_SET
        this.httpGetUserRoles().subscribe(resp => {
            if (resp.code === 0) {
                this.loginUserRoles = new Set<string>(resp.result)
            }
        })
    }

    private getAllPermChilds() {
        this.httpGetAllPermissionChilds().subscribe(resp => {
            if (resp.code == 0) {
                let map: ChildPermMap = new Map()
                for (let key of Object.keys(resp.result)) {
                    map.set(key, new Set(resp.result[key]))
                }
                this.permChilds = map
            }
        })
    }

    public login(info: LoginInfo): void {
        this.httpLogin(info).subscribe(
            response => {
                if (response.code === 0) {
                    this.loginUser = info.name
                    this.getPermsAndRoles()
                    localStorage.setItem('token_expires', response.result)
                    localStorage.setItem('login_user', info.name)
                    if (this.loginUser == 'admin') {
                        this.router.navigate(['/users'])
                    } else {
                        this.router.navigate(['/apps'])
                    }
                } else {
                    this.loginUser = ''
                    this.loginUserPerms = this.EMETY_SET
                    this.alert.warning(response.error)
                }
            })
    }

    public createUser(info: SignupInfo): Observable<ServiceResponse<User>> {
        return this.httpSignupAdmin(info)
    }

    public signup(info: SignupInfo): void {
        this.httpSignup(info).subscribe(resp => {
            if (resp.code === 0) {
                this.loginUser = info.name
                this.getPermsAndRoles()
                localStorage.setItem('token_expires', resp.result)
                localStorage.setItem('login_user', info.name)
                if (this.loginUser == 'admin') {
                    this.router.navigate(['/users'])
                } else {
                    this.router.navigate(['/apps'])
                }
                this.alert.success("注册成功")
            } else {
                this.loginUser = ''
                this.loginUserPerms = this.EMETY_SET
                this.alert.warning(resp.error)
            }
        })
    }

    public logout(): void {
        this.loginUser = ''
        this.loginUserPerms = this.EMETY_SET
        this.loginUserRoles = this.EMETY_SET
        localStorage.setItem('token_expires', '0')
        localStorage.setItem('login_user', '')
        const exp: Date = new Date()
        exp.setTime(exp.getTime() - 24 * 60 * 60_000)
        const cookie = 'rememberMe=;path=/;expires=' + exp.toUTCString() + ';SameSite=lax'
        document.cookie = cookie
        const cookie1 = 'JSESSIONID=;path=/;expires=' + exp.toUTCString() + ';SameSite=lax'
        document.cookie = cookie1
        this.httpLogout().subscribe(() => { })
    }

    public hasRole(role: string): boolean {
        return this.loginUserRoles.has(role)
    }

    public hasPerm(perm: string): boolean {
        return this.loginUserPerms.has(perm)
    }

    public hasPermOrChild(perm: string): boolean {
        let childs = this.permChilds.get(perm)
        if (childs) {
            if (this.loginUserPerms.has(perm)) {
                return true
            }
            for (let p of this.loginUserPerms) {
                if (childs.has(p)) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    public perm(name: string): boolean {
        return !this.hasPerm(name)
    }

    public perms(name: string): boolean {
        return !this.hasPermOrChild(name)
    }

    private httpLogin(info: LoginInfo): Observable<ServiceResponse<string>> {
        const url = environment.accountApiUrl + '/api/login?remember-me=on'
        const auth = btoa(info.name+":"+info.password)
        const headers: HttpHeaders = new HttpHeaders().append('Authorization', 'Basic '+auth)
        return this.httpUtils.httpPostWithHeaders('用户登录', url, '', headers)
    }

    private httpSignup(info: SignupInfo): Observable<ServiceResponse<string>> {
        const url = environment.accountApiUrl + '/api/signup'
        return this.httpUtils.httpPost('用户注册', url, info)
    }

    private httpSignupAdmin(info: SignupInfo): Observable<ServiceResponse<User>> {
        const url = environment.accountApiUrl + '/api/signup/adminCreate'
        return this.httpUtils.httpPost('用户注册', url, info)
    }

    private httpLogout(): Observable<ServiceResponse<string>> {
        const url = environment.accountApiUrl + '/api/logout'
        return this.httpUtils.httpGet('用户登出', url)
    }

    private httpGetUserPermissions(): Observable<ServiceResponse<string[]>> {
        const url = environment.accountApiUrl + '/api/user/permissions'
        return this.httpUtils.httpGet('用户权限查询', url)
    }

    private httpGetUserRoles(): Observable<ServiceResponse<string[]>> {
        const url = environment.accountApiUrl + '/api/user/roles'
        return this.httpUtils.httpGet('用户角色查询', url)
    }

    private httpGetAllPermissionChilds(): Observable<ServiceResponse<Object>> {
        const url = environment.accountApiUrl + '/api/user/permissions/childs'
        return this.httpUtils.httpGet('子权限查询', url)
    }

    public modifyPassword(oldpwd: string, pwd: string): Observable<ServiceResponse<boolean>> {
        const url = environment.accountApiUrl + '/api/user/password'
        return this.httpUtils.httpPut('修改密码', url, {oldPassword: oldpwd, newPassword:pwd})
    }

    public getOpenRegister(): Observable<ServiceResponse<boolean>> {
        const url = environment.apiUrl + '/api/sys/openRegistry'
        return this.httpUtils.httpGet('读取开放注册状态', url)
    }
}
