import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HttpClientModule } from '@angular/common/http'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { VenderModule } from '../vender.module'
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap'
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { RouterModule } from '@angular/router'
import { OperationsRoutingModule } from './operations-routing.module'
import { OperationsComponent } from './operations.component'
import { AccountModule } from '../account/account.module'
import { OperationsService } from './operations.service'
import { OperationListComponent } from './operation-list.component'
import { OperationEditComponent } from './operation-edit.component'
import { NewCodeFileDialog } from './new-code-file.dialog'

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, OperationsRoutingModule],
  declarations: [
    OperationsComponent, OperationListComponent, OperationEditComponent,
    NewCodeFileDialog
  ],
  entryComponents: [
    NewCodeFileDialog
  ],
  providers: [OperationsService],
  exports: []
})
export class OperationsModule { }
