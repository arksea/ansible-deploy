import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VenderModule } from '../vender.module';
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { AccountService } from './account.service';
import { AuthGuard } from './auth-guard';
import { LoginComponent } from './login.component';
import { SignUpComponent } from './sign-up.component';
import { AccountRoutingModule } from './account-routing.module';
import { CommonUtilsModule } from '../utils/utils.module';

@NgModule({
  imports: [
    RouterModule,
    CommonModule, FormsModule, ReactiveFormsModule,
    HttpClientModule,
    VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
    CommonUtilsModule, AccountRoutingModule],
  declarations: [
    LoginComponent,
    SignUpComponent,
  ],
  entryComponents: [
  ],
  providers: [AuthGuard, AccountService],
  exports: []
})
export class AccountModule { }
