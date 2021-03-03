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
import { AppListComponent } from './app-list.component'
import { AppEditComponent } from './app-edit.component'
import { VersionEditComponent } from './version-edit.component'
import { VersionListComponent } from './version-list.component'
import { AppComponent } from './app.component'
import { NewVersionDialog } from './new-version.dialog'
import { AddHostDialog } from './add-host.dialog'
import { JobPlayDialog, StatusJobPlayDialog, DeleteJobPlayDialog } from './job-play.dialog'
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
    VersionEditComponent, VersionListComponent, NewVersionDialog, AddHostDialog,
    JobPlayDialog,StatusJobPlayDialog,DeleteJobPlayDialog,
    PortSelectDialog, AppOperationHistoryComponent
  ],
  entryComponents: [
    NewVersionDialog, AddHostDialog,JobPlayDialog,StatusJobPlayDialog,PortSelectDialog,DeleteJobPlayDialog
  ],
  providers: [AppsService],
  exports: []
})
export class AppsModule { }
