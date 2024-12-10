import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  message: string;
  params: {};
  ok: string;
  cancel: string;
  okStyle?: string;
  cancelStyle?: string;
  isHtmlMessage?: boolean;
}

export enum DIALOG_BUTTON_STYLE {
  BTN_STYLE_ACCENT = 'accent',
  BTN_STYLE_WARN = 'warn',
  BTN_STYLE_LINK = 'link'
}

@Component({
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent {
  DIALOG_BUTTON_STYLE = DIALOG_BUTTON_STYLE;

  message: string;
  params: {};
  cancelButtonText: string;
  continuarButtonText: string;
  cancelButtonStyle: string;
  continuarButtonStyle: string;
  isHtmlMessage = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) data: DialogData,
    private dialogRef: MatDialogRef<DialogComponent>
  ) {
    if (data) {
      this.message = data.message || '';
      this.params = data.params || {};
      this.cancelButtonText = data.cancel || '';
      this.continuarButtonText = data.ok || '';
      this.cancelButtonStyle = data.cancelStyle;
      this.continuarButtonStyle = data.okStyle;
      this.isHtmlMessage = data.isHtmlMessage ?? false;
    }
  }

  /**
   * Aceptar mensaje de confirmacion
   */
  confirmacionClick(): void {
    this.dialogRef.close(true);
  }

}

