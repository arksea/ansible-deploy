import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { MessageNotify } from './message-notify';

export class ServiceResponse<T> {
    code :number;
    error? : string;
    result?: T;
    requestId?: string;
}

@Injectable()
export class HttpUtils {
    headers: HttpHeaders;

    public constructor(private http: HttpClient, private alert: MessageNotify, private router: Router) {
        this.headers = new HttpHeaders();
        this.headers = this.headers.append('Content-Type', 'application/json; charset=UTF-8');
    }

    // http mathods
    public httpPostWithOptions(requestMessage: string, url: string, body: any, options: object) {
        return this.http.post(url, body, options).pipe(
            tap(r => this.handleRestResult(r, requestMessage)),
            catchError(r => this.handleCatchedError(r, requestMessage))
        );
    }

    public httpPost(requestMessage: string, url: string, body: any) {
        return this.http.post(url, body, { headers: this.headers, withCredentials: true }).pipe(
            tap(r => this.handleRestResult(r, requestMessage)),
            catchError(r => this.handleCatchedError(r, requestMessage))
        );
    }

    public httpPut(requestMessage: string, url: string, body: any) {
        return this.http.put(url, body, { headers: this.headers, withCredentials: true }).pipe(
            tap(r => this.handleRestResult(r, requestMessage)),
            catchError(r => this.handleCatchedError(r, requestMessage))
        );
    }

    public httpGet(requestMessage: string, url: string) {
        return this.http.get(url, { headers: this.headers, withCredentials: true }).pipe(
            tap(r => this.handleRestResult(r, requestMessage)),
            catchError(r => this.handleCatchedError(r, requestMessage))
        );
    }

    public httpDelete(requestMessage: string, url: string) {
        return this.http.delete(url, { headers: this.headers, withCredentials: true }).pipe(
            tap(r => this.handleRestResult(r, requestMessage)),
            catchError(r => this.handleCatchedError(r, requestMessage))
        );
    }

    private handleCatchedError(respond: HttpErrorResponse, request: string) {
        console.info(respond);
        if (respond.status === 401) {
            this.router.navigate(['/login']);
        } else {
            if (respond.error.error) {
                console.error(respond.error.error);
                this.alert.error(request + '发生异常: '+respond.error.error);
            } else if (respond.error) {
                console.error(respond.error);
                this.alert.error(request + '发生异常: '+respond.error);
            } else if (respond.message) {
                console.error(respond.message);
                this.alert.error(request + '发生异常: '+respond.message);
            } else {
                console.error(request + '发生异常');
                this.alert.error(request + '发生异常');
            }
        }
        return new BehaviorSubject(respond.error);
    }

    private handleRestResult(result, request: string) {
        if (result.code == 0) {
            //do nothing
        } else {
            console.info(result);
            if (result.code === 401 || result.status === 401) {
                this.router.navigate(['/login']);
            } else {
                if (result.error) {
                    console.error(result.error);
                    this.alert.error(request + '失败: '+result.error);
                } else {
                    console.error(request + '失败');
                    this.alert.error(request + '失败');
                }
            }
        }
        return new BehaviorSubject(result);
    }
}
