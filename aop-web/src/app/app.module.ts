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
/////////////////////////////////////////////////////////////////////////////////////

@NgModule({
  declarations: [
    AppComponent,AppNavbarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
