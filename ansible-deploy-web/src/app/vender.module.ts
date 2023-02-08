import { NgModule } from '@angular/core'
//bootstrap
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
//material
import { MatSnackBarModule } from '@angular/material/snack-bar'
import { MatDatepickerModule } from '@angular/material/datepicker'
import { MatNativeDateModule } from '@angular/material/core'
import { MatFormFieldModule } from '@angular/material/form-field'
import { MatSelectModule } from '@angular/material/select'
import { MatDialogModule } from '@angular/material/dialog'
import { MatMomentDateModule, MomentDateModule } from '@angular/material-moment-adapter'
import { MatSlideToggleModule } from '@angular/material/slide-toggle'
import { MatPaginatorModule } from '@angular/material/paginator'
import { MatInputModule } from '@angular/material/input'
import { MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter'
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core'

@NgModule({
  exports: [
    //bootstrap
    NgbModule,
    //material
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatMomentDateModule,
    MomentDateModule,
    MatPaginatorModule,
    MatInputModule,
    MatSlideToggleModule
  ],
  providers: [
    // The locale would typically be provided on the root module of your application. We do it at
    // the component level here, due to limitations of our example generation script.
    { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' },
    // `MomentDateAdapter` and `MAT_MOMENT_DATE_FORMATS` can be automatically provided by importing
    // `MatMomentDateModule` in your applications root module. We provide it at the component level
    // here, due to limitations of our example generation script.
    // {
    //   provide: DateAdapter,
    //   useClass: MomentDateAdapter,
    //   deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    // },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS }
  ]

})
export class VenderModule { }
