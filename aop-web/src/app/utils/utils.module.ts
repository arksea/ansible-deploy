import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmDialog } from './confirm.dialog';

@NgModule({
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    NgbPaginationModule, NgbAlertModule, NgbModule],
  declarations: [
    ConfirmDialog, 
  ],
  entryComponents: [
    ConfirmDialog
  ],
  providers: [],
  exports: [ConfirmDialog]
})
export class CommonToolsModule { }
