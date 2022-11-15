import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { ProblemComponent } from '@core/component/problem.component';
import { SgiProblem } from '@core/errors/sgi-error';
import { TranslateService } from '@ngx-translate/core';

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
export class SnackBarComponent extends ProblemComponent implements OnInit {

  constructor(
    private matSnackBarRef: MatSnackBarRef<any>,
    @Inject(MAT_SNACK_BAR_DATA) public data: SnackBarData,
    readonly translate: TranslateService
  ) {
    super(translate);
  }

  ngOnInit(): void {
  }

  dismiss(): void {
    this.matSnackBarRef.dismiss();
  }

}
