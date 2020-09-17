import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators, NgModel } from '@angular/forms';
import { NgbModal,NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
@Component({
  selector: 'ports',
  templateUrl: './ports.component.html'
})
export class PortsComponent implements OnInit {

  constructor(private modal: NgbModal,
              public account: AccountService,
              private alert: MessageNotify) {
  }

  searchForm: FormGroup = new FormGroup({
    searchPrefix: new FormControl('',[Validators.required]),
  });

  ngOnInit(): void {
    //this.svc.queryHosts();
  }

  search(event: FormDataEvent) {
    event.preventDefault();
    let pre = this.searchForm.get('searchPrefix').value;
  }
}