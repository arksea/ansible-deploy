import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HttpClientModule } from '@angular/common/http'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { VenderModule } from '../vender.module'
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap'
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { RouterModule } from '@angular/router'
import { AppsRoutingModule } from './apps-routing.module'
import { AppsComponent } from './apps.component'
import { AccountModule } from '../account/account.module'
import { AppsService } from './apps.service'
import { TriggerService } from './trigger.service'
import { AppListComponent } from './app-list.component'
import { AppEditComponent } from './app-edit.component'
import { VersionEditComponent } from './version-edit.component'
import { VersionListComponent } from './version-list.component'
import { AppComponent } from './app.component'
import { AddHostDialog } from './add-host.dialog'
import { EditTriggerDialog } from './edit-trigger.dialog'
import { JobPlayDialog, StatusJobPlayDialog, DeleteJobPlayDialog } from './job-play.dialog'
import { JobLogDialog } from './job-log.dialog'
import { PortSelectDialog } from './port-select.dialog'
import { AppOperationHistoryComponent } from './app-operation-history.component'

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, AppsRoutingModule],
  declarations: [
    AppsComponent, AppListComponent, AppEditComponent, AppComponent, 
    VersionEditComponent, VersionListComponent, AddHostDialog, EditTriggerDialog,
    JobPlayDialog, StatusJobPlayDialog, DeleteJobPlayDialog, JobLogDialog,
    PortSelectDialog, AppOperationHistoryComponent
  ],
  entryComponents: [
    AddHostDialog, EditTriggerDialog, JobPlayDialog, StatusJobPlayDialog,
    PortSelectDialog, DeleteJobPlayDialog, JobLogDialog
  ],
  providers: [AppsService, TriggerService],
  exports: []
})
export class AppsModule { }
