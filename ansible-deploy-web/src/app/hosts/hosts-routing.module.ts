import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HostsComponent } from './hosts.component';
import { AuthGuard } from '../account/auth-guard';

const routes: Routes = [
  { path: 'hosts', component: HostsComponent, canActivate: [AuthGuard]  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HostsRoutingModule { }
