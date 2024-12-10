import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DIALOG_BUTTON_STYLE, DialogComponent, DialogData } from '../../block/dialog/dialog.component';

const MSG_BUTTON_OK = marker('btn.ok');
const MSG_BUTTON_CANCEL = marker('btn.cancel');

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {
  }

  showConfirmation(
    message: string,
    params: {} = {},
    ok: string = MSG_BUTTON_OK,
    cancel: string = MSG_BUTTON_CANCEL,
    okStyle: string = DIALOG_BUTTON_STYLE.BTN_STYLE_ACCENT,
    cancelStyle: string = DIALOG_BUTTON_STYLE.BTN_STYLE_LINK,
    isHtmlMessage: boolean = false
  ): Observable<boolean> {
    const dialogRef = this.dialog.open(DialogComponent, {
      ...this.dialog,
      data: {
        message,
        params,
        ok,
        cancel,
        okStyle,
        cancelStyle,
        isHtmlMessage
      } as DialogData,
      panelClass: 'confirmacion-dialog',
      disableClose: true
    });
    return dialogRef.afterClosed().pipe(
      map((confirmed: boolean) => confirmed ? confirmed : false)
    );
  }

  showConfirmationHtmlMessage(
    message: string,
    params: {} = {},
    ok: string = MSG_BUTTON_OK,
    cancel: string = MSG_BUTTON_CANCEL,
    okStyle: string = DIALOG_BUTTON_STYLE.BTN_STYLE_ACCENT,
    cancelStyle: string = DIALOG_BUTTON_STYLE.BTN_STYLE_LINK
  ): Observable<boolean> {
    return this.showConfirmation(
      message,
      params,
      ok,
      cancel,
      okStyle,
      cancelStyle,
      true);
  }

  showInfoDialog(
    message: string,
    params: {} = {},
    ok: string = MSG_BUTTON_OK,
    okStyle: string = DIALOG_BUTTON_STYLE.BTN_STYLE_ACCENT
  ): Observable<boolean> {
    const dialogRef = this.dialog.open(DialogComponent, {
      ...this.dialog,
      data: {
        message,
        params,
        ok,
        okStyle
      } as DialogData,
      panelClass: 'info-dialog',
      disableClose: true
    });
    return dialogRef.afterClosed().pipe(
      map((confirmed: boolean) => confirmed ? confirmed : false)
    );
  }

}
