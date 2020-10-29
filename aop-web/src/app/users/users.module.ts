import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VenderModule } from '../vender.module';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { UsersRoutingModule } from './users-routing.module';
import { ActiveUsersComponent, BlockedUsersComponent } from './users.component';
import { AccountModule } from '../account/account.module';
import { UsersService } from './users.service';
import { UserRolesDialog } from './user-roles.dialog';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, UsersRoutingModule],
  declarations: [
    ActiveUsersComponent, BlockedUsersComponent,UserRolesDialog
  ],
  entryComponents: [
    UserRolesDialog
  ],
  providers: [UsersService],
  exports: []
})
export class UsersModule { }
