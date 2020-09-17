import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PortsComponent } from './ports.component';
import { AuthGuard } from '../account/auth-guard';

const routes: Routes = [
  { path: 'ports', component: PortsComponent, canActivate: [AuthGuard]  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PortsRoutingModule { }
