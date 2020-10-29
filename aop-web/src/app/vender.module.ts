import {NgModule} from '@angular/core';
//bootstrap
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
//material
import {MatSnackBarModule} from '@angular/material/snack-bar';

@NgModule({
  exports: [
    //bootstrap
    NgbModule,
    //material
    MatSnackBarModule,
  ]
})
export class VenderModule {}
