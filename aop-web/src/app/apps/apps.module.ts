import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VenderModule } from '../vender.module';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { AppsRoutingModule } from './apps-routing.module';
import { AppsComponent } from './apps.component';
import { AccountModule } from '../account/account.module';
import { AppsService } from './apps.service';
import { AppListComponent } from './app-list.component';
import { AppEditComponent } from './app-edit.component';
import { AppComponent } from './app.component';
import { NewVersionDialog } from './new-version.dialog';
import { AddHostDialog } from './add-host.dialog';
import { JobPlayDialog, StatusJobPlayDialog } from './job-play.dialog';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, AppsRoutingModule],
  declarations: [
    AppsComponent, AppListComponent, AppEditComponent, AppComponent, 
    NewVersionDialog, AddHostDialog,JobPlayDialog,StatusJobPlayDialog
  ],
  entryComponents: [
    NewVersionDialog, AddHostDialog,JobPlayDialog,StatusJobPlayDialog
  ],
  providers: [AppsService],
  exports: []
})
export class AppsModule { }
