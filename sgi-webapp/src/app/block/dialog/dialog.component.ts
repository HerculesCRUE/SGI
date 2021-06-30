import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  message: string;
  params: {};
  ok: string;
  cancel: string;
}
@Component({
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent {

  message: string;
  params: {};
  cancelButtonText: string;
  continuarButtonText: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) data: DialogData,
    private dialogRef: MatDialogRef<DialogComponent>
  ) {
    if (data) {
      this.message = data.message || '';
      this.params = data.params || {};
      this.cancelButtonText = data.cancel || '';
      this.continuarButtonText = data.ok || '';
    }
  }

  /**
   * Aceptar mensaje de confirmacion
   */
  confirmacionClick(): void {
    this.dialogRef.close(true);
  }

}

