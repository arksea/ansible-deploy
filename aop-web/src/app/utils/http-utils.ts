import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { MessageNotify } from './message-notify';

export class ServiceResponse<T> {
    status: number;
    message: string;
    result: T;
    requestId: string;
    code ?:number;
    error? : string;
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
        } else if (respond.error.error) {
            this.alert.error(request + '发生异常：' + respond.error.error);
        } else if (respond.error.message) {
            this.alert.error(request + '发生异常：' + respond.error.message);
        } else if (respond.error) {
            this.alert.error(request + '发生异常：' + respond.error);
        } else {
            this.alert.error(request + '发生异常：' + respond.message);
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
            } else if (result.error) {
                this.alert.error(request + '失败：' + result.error);
            } else if (result.message) {
                this.alert.error(request + '失败：' + result.message);
            } else {
                this.alert.error(request + '失败');
            }
        }
        return new BehaviorSubject(result);
    }
}
