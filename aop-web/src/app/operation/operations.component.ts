import { Component } from '@angular/core';
import { OperationsService } from './operations.service';

@Component({
    selector: 'operations',
    templateUrl: './operations.component.html'
})
export class OperationsComponent {
    constructor(private svc: OperationsService) {
        this.svc.queryAppTypes();
    }
}
