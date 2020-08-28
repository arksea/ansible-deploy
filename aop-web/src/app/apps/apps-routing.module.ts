import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppsComponent } from './apps.component';
import { AuthGuard } from '../account/auth-guard';
import { AppEditComponent} from './app-edit.component';

const routes: Routes = [
  { path: 'apps', component: AppsComponent, canActivate: [AuthGuard]  },
  { path: 'apps/edit/:id', component: AppEditComponent, canActivate: [AuthGuard]  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppsRoutingModule { }
