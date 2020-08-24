import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NewGroupDialog } from './new-group.dialog';
import { GroupService } from './group.service';

@Component({
  selector: 'groups',
  templateUrl: './groups.component.html'
})
export class GroupsComponent implements OnInit {

  constructor(private modal: NgbModal, public svc: GroupService) {
  }

  searchForm: FormGroup = new FormGroup({
    searchPrefix: new FormControl('',[Validators.required]),
  });

  ngOnInit(): void {
    this.svc.getGroups();
  }

  search(event: FormDataEvent) {
    event.preventDefault();
    let pre = this.searchForm.get('searchPrefix').value;
  }

  newGroup() {
    this.modal.open(NewGroupDialog);
  }

}
