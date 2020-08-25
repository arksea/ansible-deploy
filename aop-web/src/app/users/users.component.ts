import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UsersService } from './users.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { User } from './users.entity';
import { switchMap,map,flatMap } from 'rxjs/operators';

@Component({
  selector: 'users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modal: NgbModal, public svc: UsersService, private alert: MessageNotify) {
  }

  searchForm: FormGroup = new FormGroup({
    searchPrefix: new FormControl('',[Validators.required]),
  });

  ngOnInit(): void {
    this.route.paramMap.pipe(
      map((params: ParamMap) => this.svc.getUsers(params.get('active'))
    )).subscribe();
  }

  search(event: FormDataEvent) {
    event.preventDefault();
    let pre = this.searchForm.get('searchPrefix').value;
  }

  newUser() {
    //this.modal.open(NewUserDialog);
  }

  blockUser(user: User) {
    let ref = this.modal.open(ConfirmDialog);
    ref.componentInstance.title = "禁用账号: "+user.name
    ref.componentInstance.message = "确认要禁用吗?"
    ref.componentInstance.detail = "此操作将把账号'"+user.name+"'标记为禁用状态，不会直接删除用户及其相关资源数据"
    ref.result.then(result => {
      if (result == "ok") {
        this.svc.blockUser(user).subscribe(succeed => {
          if (succeed) {
            this.alert.success('禁用账号成功');
          }
        });
      }
    }, resaon => {})
  }

}
