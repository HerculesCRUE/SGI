import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { HttpProblem, Problem } from '@core/errors/http-problem';
import { Group, GroupStatus, IGroup } from '@core/services/action-service';
import { BehaviorSubject, Observable, Subscription, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface DialogStatus {
  errors: boolean;
  problems: boolean;
  changes: boolean;
  complete: boolean;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class DialogActionComponent<T, R> implements OnInit, OnDestroy {

  readonly status$: BehaviorSubject<DialogStatus>;
  readonly problems$: BehaviorSubject<Problem[]>;
  private formStatus: GroupStatus;
  private problems: boolean;
  private group: IGroup;
  readonly initialized$: BehaviorSubject<boolean>;
  private initializing = false;
  protected subscriptions: Subscription[] = [];
  private edit: boolean;

  constructor(
    private readonly matDialogRef: MatDialogRef<any>,
    edit: boolean
  ) {
    this.group = new Group();
    this.edit = edit;
    this.problems = false;
    this.status$ = new BehaviorSubject<DialogStatus>({ errors: false, changes: false, complete: false, problems: this.problems });
    this.problems$ = new BehaviorSubject<Problem[]>([]);
    this.formStatus = { changes: false, errors: false, complete: false };
    this.initialized$ = new BehaviorSubject<boolean>(false);

    this.matDialogRef.disableClose = true;
  }

  /**
   * Returns the Form Group that will be loaded
   */
  protected abstract buildFormGroup(): FormGroup;

  get formGroup(): FormGroup {
    return this.group.form;
  }

  private initialize(): void {
    if (!this.initialized$.value && !this.initializing) {
      this.initializing = true;
      this.group.load(this.buildFormGroup());
      if (this.isEdit()) {
        this.group.refreshInitialState(true);
      }
      this.group.initialize();
      this.subscriptions.push(this.group.status$.subscribe((status) => {
        this.formStatus = status;
        this.mergeStatus();
      }));
      this.initialized$.next(true);
    }
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

  private pushProblems(problem: Problem | Problem[]): void {
    const current = this.problems$.value;
    if (Array.isArray(problem)) {
      this.problems$.next([...current, ...problem]);
    }
    else if (problem) {
      this.problems$.next([...current, problem]);
    }
    if (this.problems$.value.length) {
      this.setProblems(true);
    }
  }

  ngOnInit(): void {
    this.initialize();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.group.destroy();
  }

  private setProblems(value: boolean) {
    if (this.problems !== value) {
      this.problems = value;
      this.mergeStatus();
    }
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.subscriptions.push(this.saveOrUpdate().pipe(
        catchError(error => {
          if (error instanceof HttpProblem) {
            this.pushProblems(error);
            error.managed = true;
          }
          return throwError(error);
        }),
        tap((result) => this.close(result))
      ).subscribe());
    }
  }

  close(result?: R): void {
    this.matDialogRef.close(result);
  }
}
