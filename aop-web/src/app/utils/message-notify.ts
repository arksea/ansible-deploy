import { Injectable } from "@angular/core";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
@Injectable()
export class MessageNotify {
    constructor(private snackBar: MatSnackBar) {
    }

    actionButtonLabel: string = '关闭';
    autoHide: number = 5000;

    public notify(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-dark','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public light(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-light','text-dark'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public success(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-success','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public info(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-info','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public warning(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-warning','text-dark'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }

    public error(msg: string) {
        let config = new MatSnackBarConfig();
        config.duration = this.autoHide;
        config.panelClass = ['bg-danger','text-light'];
        this.snackBar.open(msg, this.actionButtonLabel, config);
    }
}
