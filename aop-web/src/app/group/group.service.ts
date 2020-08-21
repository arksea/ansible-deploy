import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { AppGroup } from './group.entity';
import { ServiceResponse } from '../utils/http-utils';
import { HttpUtils } from '../utils/http-utils';
import { MessageNotify } from "../utils/message-notify";
import { environment } from '../../environments/environment';
import { map, flatMap } from 'rxjs/operators';

// type ChildPermMap = Map<string, Set<string>>;

@Injectable()
export class GroupService {
  private EMETY_SET: Set<string> = new Set();

  public constructor(private httpUtils: HttpUtils, private router: Router, private alert : MessageNotify) {
  }


  // public createGroup(name: string, description: string): void {
  //   this.httpCreateGroup(name, description).subscribe(
  //     response => {
  //       if (response.code === 0) {
  //         this.router.navigate(['/']);
  //       } else {
  //         this.alert.warning(response.error);
  //       }
  //     });
  // }

  public createGroup(name: string, description: string): Observable<ServiceResponse<string>> {
      let n = encodeURI(name);
      let d = encodeURI(description);
      const url = environment.apiUrl + '/api/groups?name=' + n + '&desc=' + d;
      return this.httpUtils.httpPost('新建组', url, '');
  }
}
