import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { Problem } from '@core/errors/http-problem';

export interface SnackBarData {
  msg: string;
  error?: Problem;
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
