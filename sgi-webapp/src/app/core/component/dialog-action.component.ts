import { Directive, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { SgiProblem } from '@core/errors/sgi-error';
import { ErrorUtils } from '@core/utils/error-utils';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DialogFormComponent } from './dialog-form.component';

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class DialogActionComponent<R> extends DialogFormComponent<R> implements OnInit, OnDestroy {

  constructor(
    matDialogRef: MatDialogRef<any, R>,
    edit: boolean
  ) {
    super(matDialogRef, edit);
  }

  protected abstract saveOrUpdate(): Observable<R>;

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.subscriptions.push(this.saveOrUpdate().pipe(
        tap(() =>
          () => this.clearProblems(),
          () => this.clearProblems()
        ),
        catchError(error => {
          const errors: SgiProblem[] = [];
          if (Array.isArray(error)) {
            errors.push(...error);
          } else {
            errors.push(ErrorUtils.toSgiProblem(error));
          }
          errors.filter((e) => !e.managed).forEach(e => {
            e.managed = true;
            this.pushProblems(e);
          });

          return throwError(errors);
        }),
        tap((result) => this.close(result))
      ).subscribe());
    }
  }
}
