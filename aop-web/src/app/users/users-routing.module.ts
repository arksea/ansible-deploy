import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ActiveUsersComponent, BlockedUsersComponent } from './users.component';
import { AuthGuard } from '../account/auth-guard';

const routes: Routes = [
  { path: 'users',   redirectTo: 'users/active', pathMatch: 'full' },
  { path: 'users/active', component: ActiveUsersComponent, canActivate: [AuthGuard]  },
  { path: 'users/blocked', component: BlockedUsersComponent, canActivate: [AuthGuard]  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule { }
