import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AppsComponent } from './apps.component'
import { AuthGuard } from '../account/auth-guard'
import { AppListComponent } from './app-list.component'
import { AppEditComponent } from './app-edit.component'
import { AppComponent } from './app.component'
import { AppOperationHistoryComponent } from './app-operation-history.component'
import { VersionListComponent } from './version-list.component'

const routes: Routes = [
  {
    path: 'apps', component: AppsComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: AppListComponent, canActivate: [AuthGuard] },
      { path: ':id/edit', component: AppEditComponent, canActivate: [AuthGuard] },
      { path: ':id/versions', component: VersionListComponent, canActivate: [AuthGuard]},
      { path: ':id/edit/:appType', component: AppEditComponent, canActivate: [AuthGuard] },
      { path: ':id', component: AppComponent, canActivate: [AuthGuard] },
      { path: ':id/operations', component: AppOperationHistoryComponent, canActivate: [AuthGuard] },
    ]
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppsRoutingModule { }
