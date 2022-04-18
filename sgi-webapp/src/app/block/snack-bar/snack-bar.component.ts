import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { SgiProblem } from '@core/errors/sgi-error';

export interface SnackBarData {
  msg: string;
  error?: SgiProblem;
  params: {};
}

@Component({
  templateUrl: './snack-bar.component.html',
  styleUrls: ['./snack-bar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SnackBarComponent implements OnInit {

  constructor(
    private matSnackBarRef: MatSnackBarRef<any>,
    @Inject(MAT_SNACK_BAR_DATA) public data: SnackBarData
  ) { }

  ngOnInit(): void {
  }

  dismiss(): void {
    this.matSnackBarRef.dismiss();
  }

}
