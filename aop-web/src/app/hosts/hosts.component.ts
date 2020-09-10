import { Component, OnInit, ViewChild } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators, NgModel } from '@angular/forms';
import { NgbModal,NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EditHostDialog } from './edit-host.dialog';
import { HostsService } from './hosts.service';
import { ConfirmDialog } from '../utils/confirm.dialog';
import { MessageNotify } from '../utils/message-notify';
import { Host } from '../app.entity';
import { AccountService } from '../account/account.service';
@Component({
  selector: 'hosts',
  templateUrl: './hosts.component.html'
})
export class HostsComponent implements OnInit {

  constructor(private modal: NgbModal, public svc: HostsService, 
              public account: AccountService,
              private alert: MessageNotify) {
  }

  searchForm: FormGroup = new FormGroup({
    searchPrefix: new FormControl('',[Validators.required]),
  });

  ngOnInit(): void {
    this.svc.queryHosts();
  }

  search(event: FormDataEvent) {
    event.preventDefault();
    let pre = this.searchForm.get('searchPrefix').value;
  }

  newHost() {
    let ref = this.modal.open(EditHostDialog);
    ref.componentInstance.setHost(new Host());
  }

  editHost(host: Host) {
    let ref: NgbModalRef = this.modal.open(EditHostDialog);
    ref.componentInstance.setHost(host);
  }

  switchStatus(host: Host) {
    let ref = this.modal.open(ConfirmDialog);
    let opType = host.enabled?"禁用":"启用";
    ref.componentInstance.title = opType + "主机: "+host.privateIp;
    ref.componentInstance.message = "确认要" + opType + "吗?";
    ref.componentInstance.detail = "此操作将把主机'"+host.privateIp+"'标记为"+ opType + "状态, 不会删除或添加主机记录";
    ref.result.then(result => {
      if (result == "ok") {
        host.enabled = !host.enabled;
        this.svc.saveHost(host).subscribe(error => {
          if (!error) {
            this.alert.success(opType+'主机成功');
          }
        });
      }
    }, resaon => {})
  }
}