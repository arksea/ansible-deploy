import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AppTypesComponent } from './app-types.component'
import { AuthGuard } from '../account/auth-guard'
import { AppTypeListComponent } from './app-type-list.component'
import { AppTypeEditComponent } from './app-type-edit.component'

const routes: Routes = [
  {
    path: 'app-types', component: AppTypesComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: AppTypeListComponent, canActivate: [AuthGuard] },
      { path: ':id/edit', component: AppTypeEditComponent, canActivate: [AuthGuard] },
      // { path: ':id/edit/:appType', component: AppEditComponent, canActivate: [AuthGuard] },
      // { path: ':id', component: AppComponent, canActivate: [AuthGuard] },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppTypesRoutingModule { }
