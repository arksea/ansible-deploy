import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HttpClientModule } from '@angular/common/http'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { VenderModule } from '../vender.module'
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap'
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { RouterModule } from '@angular/router'
import { UsersRoutingModule } from './users-routing.module'
import { UsersComponent } from './users.component'
import { AccountModule } from '../account/account.module'
import { UsersService } from './users.service'
import { UserRolesDialog } from './user-roles.dialog'
import { UserGroupsDialog } from './user-groups.dialog'
import { NewUserDialog } from './new-user.dialog'

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    AccountModule, UsersRoutingModule],
  declarations: [
    UsersComponent, UserRolesDialog,UserGroupsDialog,NewUserDialog
  ],
  entryComponents: [
    UserRolesDialog, UserGroupsDialog,NewUserDialog
  ],
  providers: [UsersService],
  exports: []
})
export class UsersModule { }
