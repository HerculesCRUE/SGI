import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { SgiProblem } from '@core/errors/sgi-error';

@Component({
  selector: 'sgi-dialog-content',
  templateUrl: './dialog-content.component.html',
  styleUrls: ['./dialog-content.component.scss']
})
export class DialogContentComponent implements OnInit {
  problems: SgiProblem[] = [];

  private get modal(): DialogActionComponent<any> {
    return this.dialogRef.componentInstance;
  }

  constructor(private dialogRef: MatDialogRef<any, any>) { }

  ngOnInit(): void {
    this.modal.problems$.subscribe((value) => {
      if (value) {
        if (Array.isArray(value)) {
          this.problems = value;
        }
        else {
          this.problems.push(value);
        }
      }
      else {
        this.problems = [];
      }
    });
  }
}
