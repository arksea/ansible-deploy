import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from '../account/auth-guard';
import { GroupsComponent } from './groups.component';
import { GroupListComponent } from './group-list.component';
import { GroupComponent } from './group.component';


const routes: Routes = [
  {
    path: 'groups', component: GroupsComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: GroupListComponent, canActivate: [AuthGuard] },
      { path: ':id', component: GroupComponent, canActivate: [AuthGuard] }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GroupsRoutingModule { }
