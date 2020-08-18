import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
/////////////////////////////////////////////////////////////////////////////////////
//路由
import { AppRoutingModule } from './app-routing.module';
/////////////////////////////////////////////////////////////////////////////////////
//导航、公共控件
import { AppComponent } from './app.component';
import { AppNavbarComponent } from './app-navbar.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonUtilsModule } from './utils/utils.module';
/////////////////////////////////////////////////////////////////////////////////////

@NgModule({
  declarations: [
    AppComponent,AppNavbarComponent
  ],
  imports: [
    BrowserModule,BrowserAnimationsModule,
    AppRoutingModule,FormsModule,ReactiveFormsModule,
    NgbModule,
    CommonUtilsModule
  ],
  entryComponents: [
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
