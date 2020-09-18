import { Component, OnInit } from '@angular/core';
import { FormDataEvent } from '@angular/forms/esm2015';
import { FormGroup, FormControl, Validators, NgModel } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageNotify } from '../utils/message-notify';
import { AccountService } from '../account/account.service';
import { PortsService } from './ports.service';
import { PortSection } from '../app.entity';
import { EditSectionDialog } from './edit-section.dialog';
import { ConfirmDialog } from '../utils/confirm.dialog';
@Component({
    selector: 'ports',
    templateUrl: './ports.component.html'
})
export class PortsComponent implements OnInit {

    constructor(private modal: NgbModal,
        public svc: PortsService,
        public account: AccountService,
        private alert: MessageNotify) {
    }

    searchForm: FormGroup = new FormGroup({
        searchPrefix: new FormControl('', [Validators.required]),
    });

    ngOnInit(): void {
        this.svc.querySections();
    }

    search(event: FormDataEvent) {
        event.preventDefault();
        let pre = this.searchForm.get('searchPrefix').value;
    }

    newSection() {
        let ref = this.modal.open(EditSectionDialog);
        let s = new PortSection();
        s.type = this.svc.portTypesMap[1];
        ref.componentInstance.setSection(s);
    }

    editSection(section: PortSection) {

    }

    deleteSection(section: PortSection) {
        let ref = this.modal.open(ConfirmDialog);
        ref.componentInstance.title = "删除端口区间: " + section.minValue + "~" + section.maxValue;
        ref.componentInstance.message = "确认要删除吗?"
        ref.result.then(result => {
            if (result == "ok") {
                this.svc.deleteSection(section).subscribe(succeed => {
                    if (succeed) {
                        this.alert.success('已删除');
                    }
                });
            }
        }, resaon => { })


    }
}