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
import { AccountModule } from '../account/account.module';
import { NewGroupDialog } from './new-group.dialog';
import { GroupsService } from './groups.service';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, GroupsRoutingModule],
  declarations: [
    GroupsComponent,GroupListComponent,GroupComponent,NewGroupDialog
  ],
  entryComponents: [
    NewGroupDialog
  ],
  providers: [GroupsService],
  exports: []
})
export class GroupsModule { }
