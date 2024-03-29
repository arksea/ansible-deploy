import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HttpClientModule } from '@angular/common/http'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { VenderModule } from '../vender.module'
import { NgbPaginationModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap'
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { RouterModule } from '@angular/router'
import { AccountModule } from '../account/account.module'
import { AppTypesRoutingModule } from './app-types-routing.module'
import { AppTypesService } from './app-types.service'
import { AppTypesComponent } from './app-types.component'
import { AppTypeListComponent } from './app-type-list.component'
import { AppTypeEditComponent } from './app-type-edit.component'
import { NewAppVarDefineDialog } from './new-app-var-define.dialog'
import { NewVersionVarDefineDialog } from './new-version-var-define.dialog'
import { OperationListComponent } from './operation-list.component'

@NgModule({
    imports: [
        RouterModule,
        CommonModule, FormsModule, ReactiveFormsModule,
        HttpClientModule,
        VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
        AccountModule, AppTypesRoutingModule],
    declarations: [AppTypesComponent, AppTypeListComponent, AppTypeEditComponent, 
        OperationListComponent, NewAppVarDefineDialog, NewVersionVarDefineDialog],
    entryComponents: [
        NewAppVarDefineDialog, NewVersionVarDefineDialog
    ],
    providers: [AppTypesService],
    exports: []
})
export class AppTypesModule { }
