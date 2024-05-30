import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { DialogStatus } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { Group, GroupStatus, IGroup } from '@core/services/action-service';
import { FormlyFieldConfig, FormlyFormOptions } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subscription, throwError } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_FORMLY_ERROR_CONTENT = marker('error.formly.generic.message');

export enum ACTION_MODAL_MODE {
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit',
  SELECT_AND_NOTIFY = 'selectAndNotify'
}
export interface IFormlyData {
  fields: FormlyFieldConfig[];
  data: any;
  model: any;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseFormlyModalComponent<R, S> extends DialogCommonComponent implements OnInit, OnDestroy {

  ACTION_MODAL_MODE = ACTION_MODAL_MODE;

  title: string;

  readonly status$: BehaviorSubject<DialogStatus>;
  private formStatus: GroupStatus;
  private group: IGroup;
  private edit: boolean;

  protected subscriptions: Subscription[] = [];

  formlyData: IFormlyData = {
    fields: [],
    data: {},
    model: {}
  };

  options: FormlyFormOptions = {
    formState: {
      mainModel: {},
    },
  };

  get actionDisabled(): boolean {
    return (this.status$.value.errors) || (this.edit && !this.status$.value.changes) || (!this.edit && !this.status$.value.complete);
  }

  get formGroup(): FormGroup {
    return this.group.form;
  }

  constructor(
    matDialogRef: MatDialogRef<any, R>,
    edit: boolean,
    protected readonly translate: TranslateService
  ) {
    super(matDialogRef);
    this.group = new Group();
    this.edit = edit;
    this.status$ = new BehaviorSubject<DialogStatus>({ errors: false, changes: false, complete: false, problems: this.problems });
    this.formStatus = { changes: false, errors: false, complete: false };

    this.matDialogRef.disableClose = true;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected abstract getKey(): string;

  protected abstract getGender(): any;

  protected abstract saveOrUpdate(): Observable<S>;

  close(result?: any): void {
    this.matDialogRef.close(result);
  }

  protected initialize(): void {
    if (!this.initialized$.value && !this.initializing) {
      this.initializing = true;
      this.group.load(new FormGroup({}));
      this.group.initialize();

      if (!!this.initializer) {
        this.initializing = true;
        const result = this.initializer();
        if (result instanceof Observable) {
          this.subscriptions.push(
            result.pipe(
              delay(100)
            ).subscribe(
              () => {
                if (this.isEdit()) {
                  this.group.refreshInitialState(true);
                } else {
                  this.group.form.updateValueAndValidity();
                }
                this.suscribeToStatusChange();
                this.initialized$.next(true);
              },
              this.processError
            )
          );
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

  protected readonly processError: (error: Error) => void = (error: Error) => {
    if (error instanceof SgiError) {
      if (!error.managed) {
        error.managed = true;
        this.pushProblems(error);
      }
    }
    else {
      // Error incontrolado
      const sgiError = new SgiError(MSG_GENERIC_ERROR_TITLE, MSG_GENERIC_FORMLY_ERROR_CONTENT);
      sgiError.managed = true;
      this.pushProblems(sgiError);
    }
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
          if (Array.isArray(error)) {
            error.forEach(e => {
              this.processError(e);
            });
          } else {
            this.processError(error);
          }

          return throwError(this.problems$.value);
        }),
        tap((result) => this.close(result))
      ).subscribe());
    }
  }

  private setupI18N(): void {
    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);
  }

}
