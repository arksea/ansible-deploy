import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OperationsComponent } from './operations.component';
import { OperationListComponent } from './operation-list.component';
import { AuthGuard } from '../account/auth-guard';
import { OperationEditComponent } from './operation-edit.component';

const routes: Routes = [
  {
    path: 'operations', component: OperationsComponent, canActivate: [AuthGuard],
    children: [
      { path: ':id', component: OperationListComponent, canActivate: [AuthGuard] },
      { path: ':id/edit', component: OperationEditComponent, canActivate: [AuthGuard] },
      { path: ':id/edit/:appTypeId', component: OperationEditComponent, canActivate: [AuthGuard] }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OperationsRoutingModule { }
