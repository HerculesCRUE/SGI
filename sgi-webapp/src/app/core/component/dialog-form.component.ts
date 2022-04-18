import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { SgiProblem, toSgiProblem } from '@core/errors/sgi-error';
import { Group, GroupStatus, IGroup } from '@core/services/action-service';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DialogCommonComponent } from './dialog-common.component';

export interface DialogStatus {
  errors: boolean;
  problems: boolean;
  changes: boolean;
  complete: boolean;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class DialogFormComponent<R> extends DialogCommonComponent implements OnInit, OnDestroy {

  readonly status$: BehaviorSubject<DialogStatus>;
  private formStatus: GroupStatus;
  private group: IGroup;
  private edit: boolean;

  constructor(
    matDialogRef: MatDialogRef<any>,
    edit: boolean
  ) {
    super(matDialogRef);
    this.group = new Group();
    this.edit = edit;
    this.status$ = new BehaviorSubject<DialogStatus>({ errors: false, changes: false, complete: false, problems: this.problems });
    this.formStatus = { changes: false, errors: false, complete: false };

    this.matDialogRef.disableClose = true;
  }

  /**
   * Returns the Form Group that will be loaded
   */
  protected abstract buildFormGroup(): FormGroup;

  get formGroup(): FormGroup {
    return this.group.form;
  }

  protected initialize(): void {
    if (!this.initialized$.value && !this.initializing) {
      this.initializing = true;
      this.group.load(this.buildFormGroup());
      if (this.isEdit()) {
        this.group.refreshInitialState(true);
      }
      this.group.initialize();

      if (!!this.initializer) {
        this.initializing = true;
        const result = this.initializer();
        if (result instanceof Observable) {
          this.subscriptions.push(result.subscribe(
            () => {
              this.suscribeToStatusChange();
              this.initialized$.next(true);
            },
            this.processError
          ));
        }
        else {
          this.suscribeToStatusChange();
          this.initialized$.next(true);
        }
      }
    }
  }

  private suscribeToStatusChange(): void {
    this.subscriptions.push(this.group.status$.subscribe((status) => {
      this.formStatus = status;
      this.mergeStatus();
    }));
    this.subscriptions.push(this.problems$.subscribe(() => {
      this.mergeStatus();
    }));
  }

  isEdit(): boolean {
    return this.edit;
  }

  protected abstract getValue(): R;

  protected abstract saveOrUpdate(): Observable<R>;

  private mergeStatus(): void {
    const current: DialogStatus = this.status$.value;
    const errors = this.formStatus.errors;
    const changes = this.formStatus.changes;
    const problems = this.problems;
    const complete = this.formStatus.complete;
    let update = false;
    if (current.errors !== errors) {
      current.errors = errors;
      update = true;
    }
    if (current.changes !== changes) {
      current.changes = changes;
      update = true;
    }
    if (current.complete !== complete) {
      current.complete = complete;
      update = true;
    }
    if (current.problems !== problems) {
      current.problems = problems;
      update = true;
    }
    if (update) {
      this.status$.next(current);
    }
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.group.destroy();
  }

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
            errors.push(toSgiProblem(error));
          }

          errors.forEach(e => {
            e.managed = true;
            this.pushProblems(e);
          });

          return throwError(errors);
        }),
        tap((result) => this.close(result))
      ).subscribe());
    }
  }

  close(result?: R): void {
    this.matDialogRef.close(result);
  }
}
