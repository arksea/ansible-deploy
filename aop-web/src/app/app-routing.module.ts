import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './account/auth-guard';
import { AppComponent } from './app.component';
import { AppHomeComponent } from './app-home.component';

const routes: Routes = [
  // { path: '',   redirectTo: 'home', pathMatch: 'full' },
  // { path: 'home', component: AppHomeComponent, canActivate: [AuthGuard]},
  { path: '**', component: AppHomeComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
