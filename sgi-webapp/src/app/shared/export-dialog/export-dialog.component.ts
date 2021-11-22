import { Component, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { BaseExportModalComponent } from '@core/component/base-export-modal/base-export-modal.component';

@Component({
  selector: 'sgi-export-dialog',
  templateUrl: './export-dialog.component.html',
  styleUrls: ['./export-dialog.component.scss']
})
export class ExportDialogComponent<T> {

  @Input()
  title = '';

  get modal(): BaseExportModalComponent<T> {
    return this.dialogRef.componentInstance;
  }

  constructor(private dialogRef: MatDialogRef<any, any>) {
    dialogRef.addPanelClass('sgi-dialog-container');
  }

}
