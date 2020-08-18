import { Component } from '@angular/core'
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'
import { ConfirmDialog } from './utils/confirm.dialog'
import { AppMessageNotify } from './utils/app-message-notify'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(private modalService: NgbModal, public notify: AppMessageNotify) {
  }
  
  public open() {
    const ref = this.modalService.open(ConfirmDialog);
    ref.componentInstance.title = 'Title';
    ref.componentInstance.message = 'Hello World!';
    ref.componentInstance.detail = 'This is a bootstrap modal dialog';
  }
}
