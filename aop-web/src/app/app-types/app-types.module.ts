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


@NgModule({
    imports: [
        RouterModule,
        CommonModule, FormsModule, ReactiveFormsModule,
        HttpClientModule,
        VenderModule, NgbPaginationModule, NgbAlertModule, NgbModule,
        AccountModule, AppTypesRoutingModule],
    declarations: [AppTypesComponent, AppTypeListComponent, AppTypeEditComponent],
    entryComponents: [

    ],
    providers: [AppTypesService],
    exports: []
})
export class AppTypesModule { }
