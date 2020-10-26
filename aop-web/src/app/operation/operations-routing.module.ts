import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OperationsComponent } from './operations.component';
import { AuthGuard } from '../account/auth-guard';
import { OperationEditComponent } from './operation-edit.component';

const routes: Routes = [
  {
    path: 'operations', component: OperationsComponent, canActivate: [AuthGuard],
    children: [
      { path: ':id', component: OperationEditComponent, canActivate: [AuthGuard] },
      { path: ':id/app-type/:appTypeId', component: OperationEditComponent, canActivate: [AuthGuard] }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OperationsRoutingModule { }
