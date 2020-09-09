import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VenderModule } from '../vender.module';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { GroupsComponent } from './groups.component';
import { GroupsRoutingModule } from './groups-routing.module';
import { GroupListComponent } from './group-list.component';
import { GroupComponent } from './group.component';
import { GroupMembersComponent } from './group-members.component';
import { GroupAppsComponent } from './group-apps.component';
import { GroupHostsComponent } from './group-hosts.component';
import { AccountModule } from '../account/account.module';
import { NewGroupDialog } from './new-group.dialog';
import { GroupsService } from './groups.service';
import { HostsModule } from '../hosts/hosts.module';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, HostsModule, GroupsRoutingModule],
  declarations: [
    GroupsComponent,GroupListComponent,
    GroupComponent,GroupMembersComponent,GroupAppsComponent,GroupHostsComponent,
    NewGroupDialog
  ],
  entryComponents: [
    NewGroupDialog
  ],
  providers: [GroupsService],
  exports: []
})
export class GroupsModule { }
