import { Injectable } from "@angular/core";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
@Injectable()
export class AppMessageNotify {
    constructor(private snackBar: MatSnackBar) {
    }
    
    actionButtonLabel: string = '关闭';
    autoHide: number = 5000;

    public success(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-success','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public info(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['app-notify-info','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public warning(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['app-notify-warning','text-dark'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public error(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['app-notify-error','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }
}
