import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AppTypesComponent } from './app-types.component'
import { AuthGuard } from '../account/auth-guard'
import { AppTypeListComponent } from './app-type-list.component'
import { AppTypeEditComponent } from './app-type-edit.component'
import { OperationListComponent } from './operation-list.component';

const routes: Routes = [
  {
    path: 'app-types', component: AppTypesComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: AppTypeListComponent, canActivate: [AuthGuard] },
      { path: ':id', component: AppTypeEditComponent, canActivate: [AuthGuard] },
      { path: ':id/operations', component: OperationListComponent, canActivate: [AuthGuard] },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppTypesRoutingModule { }
