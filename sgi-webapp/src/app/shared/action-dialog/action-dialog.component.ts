import { Component, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogActionComponent } from '@core/component/dialog-action.component';

@Component({
  selector: 'sgi-action-dialog',
  templateUrl: './action-dialog.component.html',
  styleUrls: ['./action-dialog.component.scss']
})
export class ActionDialogComponent {

  @Input()
  title: string;

  get modal(): DialogActionComponent<any, any> {
    return this.dialogRef.componentInstance;
  }

  constructor(private dialogRef: MatDialogRef<any, any>) {
    dialogRef.addPanelClass('sgi-dialog-container');
  }

  action() {
    this.modal.doAction();
  }
}
