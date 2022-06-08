import { AfterViewInit, Directive, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { DialogHeaderComponent } from '@shared/dialog-header/dialog-header.component';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_ERROR_CONTENT = marker('error.generic.message');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class DialogCommonComponent implements OnInit, OnDestroy, AfterViewInit {

  readonly problems$: BehaviorSubject<SgiProblem[]>;
  get problems(): boolean {
    return this._problems;
  }
  // tslint:disable-next-line: variable-name
  private _problems: boolean;

  readonly initialized$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected initializing = false;

  protected subscriptions: Subscription[] = [];

  @ViewChild(DialogHeaderComponent) private dialogHeader!: DialogHeaderComponent;

  protected initializer: () => Observable<void> | void = () => { };

  protected readonly processError: (error: Error) => void = (error: Error) => {
    if (error instanceof SgiError) {
      if (!error.managed) {
        error.managed = true;
        this.pushProblems(error);
      }
    }
    else {
      // Error incontrolado
      const sgiError = new SgiError(MSG_GENERIC_ERROR_TITLE, MSG_GENERIC_ERROR_CONTENT);
      sgiError.managed = true;
      this.pushProblems(sgiError);
    }
  }

  constructor(
    protected readonly matDialogRef: MatDialogRef<any>
  ) {
    this._problems = false;
    this.problems$ = new BehaviorSubject<SgiProblem[]>([]);
    this.matDialogRef.disableClose = true;
    this.matDialogRef.addPanelClass('sgi-dialog-container');
  }

  ngOnInit(): void {
    this.initialize();
  }

  ngAfterViewInit(): void {
    if (this.dialogHeader) {
      this.subscriptions.push(this.dialogHeader.closeClick.subscribe(() => this.close()));
    }
  }

  protected initialize(): void {
    if (!this.initialized$.value && !this.initializing) {
      if (!!this.initializer) {
        this.initializing = true;
        const result = this.initializer();
        if (result instanceof Observable) {
          this.subscriptions.push(result.subscribe(() => this.initialized$.next(true), this.processError));
        }
        else {
          this.initialized$.next(true);
        }
      }
    }
  }

  protected pushProblems(problem: SgiProblem | SgiProblem[]): void {
    const current = this.problems$.value;
    if (Array.isArray(problem)) {
      this.problems$.next([...current, ...problem]);
    }
    else if (problem) {
      this.problems$.next([...current, problem]);
    }
    if (this.problems$.value.length) {
      this._problems = true;
    }
  }

  protected clearProblems(): void {
    this.problems$.next([]);
    this._problems = false;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  close(result?: any): void {
    this.matDialogRef.close(result);
  }

}
