import { Component, OnInit } from '@angular/core';
import { AppTypesService } from './app-types.service';

@Component({
    selector: 'app-types',
    templateUrl: './app-types.component.html'
})
export class AppTypesComponent implements OnInit {

    constructor(private svc: AppTypesService) {
    }

    ngOnInit(): void {
    }
}
