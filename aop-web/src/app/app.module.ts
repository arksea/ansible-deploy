import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
/////////////////////////////////////////////////////////////////////////////////////
//Material与Bootstrap控件
import { VenderModule } from './vender.module';
/////////////////////////////////////////////////////////////////////////////////////
//路由
import { AppRoutingModule } from './app-routing.module';
/////////////////////////////////////////////////////////////////////////////////////
//导航、公共控件
import { AppComponent } from './app.component';
import { AppHomeComponent } from './app-home.component';
import { CommonUtilsModule } from './utils/utils.module';
//登录与权限
import { AccountModule } from './account/account.module';
//组管理
import { GroupsModule } from './groups/groups.module';
//用户管理
import { UsersModule } from './users/users.module';
//应用管理
import { AppsModule } from './apps/apps.module';
//主机管理
import { HostsModule } from './hosts/hosts.module';
//端口管理
import { PortsModule } from './ports/ports.module';
//操作管理
import { OperationsModule } from './operation/operations.module';
/////////////////////////////////////////////////////////////////////////////////////

@NgModule({
  declarations: [
    AppComponent,AppHomeComponent
  ],
  imports: [
    //系统模块
    BrowserModule,BrowserAnimationsModule,FormsModule,ReactiveFormsModule,
    //导航与公共模块
    VenderModule, CommonUtilsModule,
    //登录与权限
    AccountModule,
    GroupsModule,
    UsersModule,
    AppsModule,
    HostsModule,
    PortsModule,
    OperationsModule,
    //路由
    AppRoutingModule, //主路由必须在最后，以避免屏蔽各子模块自己的路由
  ],
  entryComponents: [],
  providers: [],
  exports: [AppHomeComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
