import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VenderModule } from '../vender.module';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { AccountModule } from '../account/account.module';
import { HostsRoutingModule } from './hosts-routing.module';
import { HostsComponent } from './hosts.component';
import { EditHostDialog } from './edit-host.dialog';
import { HostsService } from './hosts.service';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule,HostsRoutingModule],
  declarations: [
    HostsComponent, EditHostDialog
  ],
  entryComponents: [
    EditHostDialog
  ],
  providers: [HostsService],
  exports: []
})
export class HostsModule { }
