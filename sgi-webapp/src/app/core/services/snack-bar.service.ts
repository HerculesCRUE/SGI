import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Problem } from '@core/errors/http-problem';
import { SnackBarComponent, SnackBarData } from '../../block/snack-bar/snack-bar.component';

@Injectable({
  providedIn: 'root',
})
export class SnackBarService {

  constructor(
    private snackBar: MatSnackBar,
  ) { }

  private open(msg: string, params: {}, error: Problem, cssClass: string): void {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.verticalPosition = 'top';
    if (!!!error) {
      snackBarConfig.duration = 4000;
    }

    this.snackBar.openFromComponent(SnackBarComponent, {
      ...snackBarConfig,
      data: {
        msg,
        params,
        error,
      } as SnackBarData,
      panelClass: cssClass,
    });
  }

  /**
   * Muestra un mensaje de error.
   *
   * @param mensaje el mensaje o lista de mensajes de error.
   */
  showError(msg: string | Problem, params: {} = {}): void {
    if (typeof msg === 'string') {
      this.open(msg, params, undefined, 'error-snack-bar');
    }
    else {
      this.open(undefined, params, msg, 'error-snack-bar');
    }
  }

  /**
   * Muestra un mensaje de exito.
   *
   * @param mensaje el mensaje.
   */
  showSuccess(msg: string, params: {} = {}): void {
    this.open(msg, params, undefined, 'success-snack-bar');
  }
}
