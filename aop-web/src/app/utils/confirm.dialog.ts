import {Component,Input} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'confirm-dialog',
  templateUrl: './confirm.dialog.html'
})
export class ConfirmDialog {
  @Input() title: string = '请确认'
  @Input() message: string = ''
  @Input() detail: string = ''
  @Input() cancel: string = '取消'
  @Input() ok: string = '确认'
  constructor(public modal: NgbActiveModal) {
  }
}
