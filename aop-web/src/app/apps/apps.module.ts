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
// import { NewGroupDialog } from './new-group.dialog';
import { AppsService } from './apps.service';
import { AppEditComponent } from './app-edit.component';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, AppsRoutingModule],
  declarations: [
    AppsComponent, AppEditComponent
  ],
  entryComponents: [

  ],
  providers: [AppsService],
  exports: []
})
export class AppsModule { }
