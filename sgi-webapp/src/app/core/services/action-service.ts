import { Directive, OnDestroy } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup } from '@angular/forms';
import { HttpProblem, Problem } from '@core/errors/http-problem';
import { DateTime } from 'luxon';
import { BehaviorSubject, from, Observable, of, Subject, Subscription, throwError } from 'rxjs';
import { catchError, defaultIfEmpty, filter, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface IActionService {
  /**
   * Status of the action
   */
  status$: BehaviorSubject<ActionStatus>;
  /**
   * Save or update the all parts of the form
   */
  saveOrUpdate(): Observable<void>;
  /**
   * Returns true if any fragment have errors
   */
  hasErrors(): boolean;
  /**
   * Returns true if any fragment have changes afther initialization
   */
  hasChanges(): boolean;
  /**
   * Validate the fragments.
   * The status is updated.
   * @param markAllTouched To force each form control marked as touched
   */
  performChecks(markAllTouched?: boolean): void;
  /**
   * Returns a registered fragment by their name
   * @param name The name of the fragment
   */
  getFragment(name: string): IFragment;
  /**
   * Returns the action links associated to the action.
   */
  getActionLinks(): ActionLink[];
}

export interface GroupStatus {
  /**
   * The nested form group is filled. Only meaningful on creation
   */
  complete: boolean;
  /**
   * The nested from group has any error
   */
  errors: boolean;
  /**
   * The nested form group has any change
   */
  changes: boolean;
}

export interface ActionStatus {
  /**
   * All fragments are filled. Only meaningful on creation
   */
  complete: boolean;
  /**
   * Any fragment has a validation error
   */
  errors: boolean;
  /**
   * Any fragment has a problem
   */
  problems: boolean;
  /**
   * Any fragment has a change
   */
  changes: boolean;
  /**
   * Any fragment is in edition mode
   */
  edit: boolean;
}

export interface IGroup {
  /**
   * Asociated form group
   */
  form: FormGroup;
  /**
   * Status of the group
   */
  status$: BehaviorSubject<GroupStatus>;
  /**
   * Returns true if any form control has an error
   */
  hasErrors(): boolean;
  /**
   * Returns true if the value of any form control has been changed after initialization
   */
  hasChanges(): boolean;
  /**
   * Returns true if all form control have the VALID status
   */
  isValid(): boolean;
  /**
   * Force the validation of all form controls
   * @param markAllTouched Mark all form controls as visited. False by default.
   */
  forceUpdate(markAllTouched?: boolean): void;
  /**
   * Reinitialice the group using current values. The group status is restored to no changes and no errors,
   * and all form controls are marked as untouched.
   * @param transitionToEdit When true the group transit to edition mode. False by default
   */
  refreshInitialState(transitionToEdit?: boolean): void;
  /**
   * Initialice the group
   */
  initialize(): void;
  /**
   * Apply the provided values to the form without modifiying the status. Must be invoked before initialization.
   * @param value The values to apply
   */
  patch(value: { [key: string]: any });
  /**
   * Load the provided form group in the group. Must be invoked before initialization.
   * @param form The form group to load
   */
  load(form: FormGroup): void;
  /**
   * Destroy the group and form group
   */
  destroy(): void;
}

export interface FragmentStatus {
  /**
   * The fragment has any validation error
   */
  errors: boolean;
  /**
   * The fragment has any change
   */
  changes: boolean;
  /**
   * The fragment are filled. Only meaningful on creation
   */
  complete: boolean;
  /**
   * The fragment is in edition mode
   */
  edit: boolean;
  /**
   * The fragment has any problem
   */
  problems: boolean;
}

export interface IFragment {
  /**
   * Status of the fragment
   */
  status$: BehaviorSubject<FragmentStatus>;
  /**
   * Problems of the fragment
   */
  problems$: Subject<Problem[]>;
  /**
   * Returns true if the fragment is in edition mode
   */
  isEdit(): boolean;
  /**
   * Initialize the fragment
   */
  initialize(): void;
  /**
   * Returns true if the fragment has any error
   */
  hasErrors(): boolean;
  /**
   * Returns true if the fragment has any problem
   */
  hasProblems(): boolean;
  /**
   * Push a related problem or problems
   */
  pushProblems(problem: Problem | Problem[]): void;
  /**
   * Clear problems
   */
  clearProblems(): void;
  /**
   * Return true if the fragment has any changes
   */
  hasChanges(): boolean;
  /**
   * Returns true when the fragment is fullfilled. Only meaningful on creation
   */
  isComplete(): boolean;
  /**
   * Returns true if the fragment have been initialized
   */
  isInitialized(): boolean;
  /**
   * Restore the initial status of the fragment using current values. The status is changed to no errors and no changes.
   * @param transitionToEdit When true the fragment change to edition mode. False by default
   */
  refreshInitialState(transitionToEdit?: boolean): void;
  /**
   * Destroy the fragment
   */
  destroy(): void;
  /**
   * Set the complete status of the fragment
   * @param value Complete value to set
   */
  setComplete(value: boolean): void;
  /**
   * Set the changes status of the fragment
   * @param value Changes value to set
   */
  setChanges(value: boolean): void;
  /**
   * Set the errors status of the fragment
   * @param value Erros value to set
   */
  setErrors(value: boolean): void;
  /**
   * Perform the persist of the value represented by the fragment. Can return an identity key related to the fragment
   */
  saveOrUpdate(): Observable<void | string | number>;
  /**
   * Return the identity key related to the fragment
   */
  getKey(): string | number;
  /**
   * Set the identity key related to the fragment
   * @param value The key to set
   */
  setKey(value: string | number): void;
  /**
   * Validate the fragment to check errors or changes
   * @param markAllTouched When true all form controls are marked as touched. False by default
   */
  performChecks(markAllTouched?: boolean): void;
}

export interface IFormFragment<T> extends IFragment {
  /**
   * Returns the asociated form group
   */
  getFormGroup(): FormGroup;
  /**
   * Returns the value that represent the form fragment
   */
  getValue(): T;
}

export interface ActionLink {
  /**
   * Title of the action link
   */
  title: string;
  /**
   * Params for de title of the action link
   */
  titleParams?: { [key: string]: any };
  /**
   * Router link of the action link
   */
  routerLink: string | string[];

  /**
   * Query params
   */
  queryParams?: {};
}

export abstract class Fragment implements IFragment {
  readonly status$: BehaviorSubject<FragmentStatus>;
  readonly problems$: BehaviorSubject<Problem[]>;
  private key: number | string;
  private edit: boolean;
  readonly initialized$: BehaviorSubject<boolean>;
  private initialing = false;
  protected subscriptions: Subscription[] = [];

  /**
   * Default constructor
   * @param key The identity key to use during initialization
   */
  constructor(key: number | string) {
    this.key = key;
    this.edit = key ? true : false;
    this.status$ = new BehaviorSubject<FragmentStatus>({ errors: false, changes: false, complete: false, edit: this.edit, problems: false });
    this.problems$ = new BehaviorSubject<Problem[]>([]);
    this.initialized$ = new BehaviorSubject<boolean>(false);
  }

  isEdit(): boolean {
    return this.edit;
  }

  initialize(): void {
    if (!this.initialized$.value && !this.initialing) {
      this.initialing = true;
      const result = this.onInitialize();
      if (result instanceof Observable) {
        this.subscriptions.push(result.subscribe(null, null, () => this.initialized$.next(true)));
      }
      else {
        this.initialized$.next(true);
      }
    }
  }

  /**
   * Hook for initialization. Called when fragment is initialized
   */
  protected abstract onInitialize(): void | Observable<any>;

  hasErrors(): boolean {
    return this.status$.value.errors;
  }

  hasProblems(): boolean {
    return this.status$.value.problems;
  }

  pushProblems(problem: Problem | Problem[]): void {
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

  clearProblems(): void {
    this.problems$.next([]);
    this.setProblems(false);
  }

  hasChanges(): boolean {
    return this.status$.value.changes;
  }

  isComplete(): boolean {
    return this.status$.value.complete;
  }

  isInitialized(): boolean {
    return this.initialized$.value;
  }

  refreshInitialState(transitionToEdit?: boolean): void {
    if (transitionToEdit) {
      this.edit = true;
    }
    this.problems$.next([]);
    this.status$.next({ errors: false, changes: false, complete: false, edit: this.isEdit(), problems: false });
  }

  destroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  setComplete(value: boolean): void {
    const current = this.status$.value;
    if (current.complete !== value) {
      current.complete = value;
      this.status$.next(current);
    }
  }

  setChanges(value: boolean): void {
    const current = this.status$.value;
    if (current.changes !== value) {
      current.changes = value;
      this.status$.next(current);
    }
  }

  setErrors(value: boolean): void {
    const current = this.status$.value;
    if (current.errors !== value) {
      current.errors = value;
      this.status$.next(current);
    }
  }

  private setProblems(value: boolean): void {
    const current = this.status$.value;
    if (current.problems !== value) {
      current.problems = value;
      this.status$.next(current);
    }
  }

  abstract saveOrUpdate(): Observable<string | number | void>;

  getKey(): string | number {
    return this.key;
  }

  setKey(value: string | number): void {
    if (!this.key) {
      this.key = value;
    }
  }

  performChecks(markAllTouched?: boolean): void {

  }
}

export abstract class FormFragment<T> implements IFormFragment<T> {
  readonly status$: BehaviorSubject<FragmentStatus>;
  readonly problems$: BehaviorSubject<Problem[]>;
  private formStatus: GroupStatus;
  private complementStatus: FragmentStatus;
  private auxiliarStatus: boolean;
  private group: IGroup;
  readonly initialized$: BehaviorSubject<boolean>;
  private initializing = false;
  protected subscriptions: Subscription[] = [];
  private key: number | string;
  private edit: boolean;

  get formGroupStatus$(): BehaviorSubject<GroupStatus> {
    return this.group.status$;
  }

  /**
   * Default constructor
   * @param key The identity key to use during initialization
   * @param enableComplementaryStatus Enable the management of fragment. False by default
   */
  constructor(key: number | string, enableComplementaryStatus = false) {
    this.group = new Group();
    this.key = key;
    this.edit = key ? true : false;
    this.auxiliarStatus = enableComplementaryStatus;
    this.status$ = new BehaviorSubject<FragmentStatus>({ errors: false, changes: false, complete: false, edit: this.edit, problems: false });
    this.problems$ = new BehaviorSubject<Problem[]>([]);
    this.formStatus = { changes: false, errors: false, complete: false };
    this.complementStatus = { errors: false, changes: false, complete: !this.edit && !enableComplementaryStatus, edit: this.edit, problems: false };
    this.initialized$ = new BehaviorSubject<boolean>(false);
  }

  /**
   * Returns the Form Group that will be loaded by the fragment
   */
  protected abstract buildFormGroup(): FormGroup;

  /**
   * Builds the patch values to apply on FormGroup during initialization
   * @param value The represatation of the form fragment to use to build the patch
   */
  protected abstract buildPatch(value: T): { [key: string]: any };

  getFormGroup(): FormGroup {
    return this.group.form;
  }

  getKey(): string | number {
    return this.key;
  }

  setKey(value: string | number): void {
    if (!this.key) {
      this.key = value;
    }
  }

  initialize(): void {
    if (!this.initialized$.value && !this.initializing) {
      this.initializing = true;
      this.group.load(this.buildFormGroup());
      if (this.key) {
        this.initializer(this.key).subscribe((initialValue) => {
          this.group.patch(this.buildPatch(initialValue));
          this.group.initialize();
          this.subscriptions.push(this.group.status$.subscribe((status) => {
            this.formStatus = status;
            this.mergeStatus();
          }));
          this.initialized$.next(true);
        });
      }
      else {
        this.group.initialize();
        this.subscriptions.push(this.group.status$.subscribe((status) => {
          this.formStatus = status;
          this.mergeStatus();
        }));
        this.initialized$.next(true);
      }
    }
  }

  isEdit(): boolean {
    return this.edit;
  }

  isInitialized(): boolean {
    return this.initialized$.value;
  }

  /**
   * Returns the initial values of the form fragment.
   * Only called if exists a key before initialization (provided on constructor)
   * @param key The identity key to use
   */
  protected abstract initializer(key: number | string): Observable<T>;

  abstract getValue(): T;

  abstract saveOrUpdate(): Observable<void | string | number>;

  private mergeStatus(): void {
    const current: FragmentStatus = this.status$.value;
    const errors = this.formStatus.errors || this.complementStatus.errors;
    const changes = this.formStatus.changes || this.complementStatus.changes;
    const problems = this.complementStatus.problems;
    const complete = !this.isEdit() && this.isValid() && this.complementStatus.complete;
    const edit = this.isEdit();
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
    if (current.edit !== edit) {
      current.edit = edit;
      update = true;
    }
    if (update) {
      this.status$.next(current);
    }
  }

  hasErrors(): boolean {
    return this.status$.value.errors;
  }

  hasProblems(): boolean {
    return this.status$.value.problems;
  }

  pushProblems(problem: Problem | Problem[]): void {
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

  clearProblems(): void {
    this.problems$.next([]);
    this.setProblems(false);
  }

  hasChanges(): boolean {
    return this.status$.value.changes;
  }

  isComplete(): boolean {
    return this.status$.value.complete;
  }

  private isValid(): boolean {
    return this.group.isValid();
  }

  performChecks(markAllTouched = false): void {
    this.group.forceUpdate(markAllTouched);
  }

  refreshInitialState(transitionToEdit?: boolean): void {
    this.group.refreshInitialState(transitionToEdit);
    if (transitionToEdit) {
      this.edit = true;
    }
    this.problems$.next([]);
    this.complementStatus = { errors: false, changes: false, complete: false, edit: this.isEdit(), problems: false };
    this.mergeStatus();
  }

  destroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.group.destroy();
  }

  setComplete(value: boolean) {
    if (this.auxiliarStatus) {
      const current = this.complementStatus;
      if (current.complete !== value) {
        current.complete = value;
        this.mergeStatus();
      }
    }
  }

  setChanges(value: boolean) {
    if (this.auxiliarStatus) {
      const current = this.complementStatus;
      if (current.changes !== value) {
        current.changes = value;
        this.mergeStatus();
      }
    }
  }

  setErrors(value: boolean) {
    if (this.auxiliarStatus) {
      const current = this.complementStatus;
      if (current.errors !== value) {
        current.errors = value;
        this.mergeStatus();
      }
    }
  }

  private setProblems(value: boolean) {
    const current = this.complementStatus;
    if (current.problems !== value) {
      current.problems = value;
      this.mergeStatus();
    }
  }
}

export class Group implements IGroup {
  form: FormGroup;
  status$: BehaviorSubject<GroupStatus> = new BehaviorSubject<GroupStatus>({ errors: false, changes: false, complete: false });
  private initialState: any;
  private subscriptions: Subscription[] = [];
  private editing: boolean;
  private forcingUpdate = false;
  initialized = false;

  load(form: FormGroup): void {
    this.form = form;
    this.initialState = form.getRawValue();
  }

  initialize(): void {
    if (!this.initialized) {
      this.subscriptions.push(this.form.valueChanges.subscribe((ev) => {
        if (this.editing) {
          this.checkChanges();
          if (this.isValid()) {
            this.publishComplete(true);
          }
        }
        else if (!this.forcingUpdate) {
          this.publishChanges(true);
        }
        this.forcingUpdate = false;
      }));

      this.subscriptions.push(this.form.statusChanges.subscribe((ev) => {
        this.checkErrors();
        if (!this.editing) {
          if (this.isValid() && !this.hasErrors()) {
            this.publishComplete(true);
          }
          else {
            this.publishComplete(false);
          }
        }
      }));
      this.initialized = true;
    }
  }

  patch(value: { [key: string]: any }) {
    this.form.patchValue(value, { onlySelf: false, emitEvent: true });
    this.initialState = this.form.getRawValue();
    this.editing = true;
  }

  private publishErrors(value: boolean) {
    const current = this.status$.value;
    if (current.errors !== value) {
      current.errors = value;
      this.status$.next(current);
    }
  }

  private publishChanges(value: boolean) {
    const current = this.status$.value;
    if (current.changes !== value) {
      current.changes = value;
      this.status$.next(current);
    }
  }

  private publishComplete(value: boolean) {
    const current = this.status$.value;
    if (current.complete !== value) {
      current.complete = value;
      this.status$.next(current);
    }
  }

  private hasControlErrors(form: AbstractControl): boolean {
    if (form instanceof FormControl) {
      // Return FormControl errors or null
      if (this.editing || (!this.editing && (form.touched || !form.pristine))) {
        return (form.errors ? true : false);
      }
      return false;
    }
    if (form instanceof FormGroup) {
      if (form.errors) {
        return true;
      }
      return Object.keys(form.controls).find((key) => this.hasControlErrors(form.get(key))) ? true : false;
    }
    if (form instanceof FormArray) {
      if (form.errors) {
        return true;
      }
      return form.controls.some(control => this.hasControlErrors(control));
    }
  }

  private isControlValid(form: AbstractControl): boolean {
    if (form instanceof FormControl) {
      // VALID and DISABLED are mutually exclusive. A disabled control isn't valid, but are allowed.
      return form.valid || form.disabled;
    }
    if (form instanceof FormGroup) {
      if (!form.valid) {
        return false;
      }
      return Object.keys(form.controls).find((key) => !this.isControlValid(form.get(key))) ? false : true;
    }
    if (form instanceof FormArray) {
      if (!form.valid) {
        return false;
      }
      return !form.controls.some(control => !this.isControlValid(control));
    }
  }

  hasErrors(): boolean {
    return this.status$.value.errors;
  }

  hasChanges(): boolean {
    if (this.editing) {
      return this.status$.value.changes;
    }
    else {
      return this.form ? !this.deepEquals(this.initialState, this.form.getRawValue()) : false;
    }
  }

  isValid(): boolean {
    if (this.form) {
      return this.isControlValid(this.form);
    }
    return true;
  }

  private checkChanges(): void {
    let value = false;
    if (this.form) {
      value = !this.deepEquals(this.initialState, this.form.getRawValue());
    }
    this.publishChanges(value);
  }

  private checkErrors(): void {
    let value = false;
    if (this.form) {
      value = this.hasControlErrors(this.form);
    }
    this.publishErrors(value);
  }

  forceUpdate(markAllTouched = false): void {
    if (!this.form) {
      return;
    }
    if (markAllTouched) {
      this.form.markAllAsTouched();
    }
    this.forcingUpdate = true;
    this.form.updateValueAndValidity();
  }

  destroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  refreshInitialState(transitionToEdit?: boolean): void {
    this.initialState = this.form ? this.form.getRawValue() : undefined;
    if (transitionToEdit) {
      this.editing = true;
    }
    if (this.form) {
      this.form.reset(this.initialState, { onlySelf: false, emitEvent: false });
    }
    this.status$.next({ errors: false, changes: false, complete: false });
  }

  private deepEquals(x: any, y: any, root = true): boolean {
    if (x === y) {
      return true; // if both x and y are null or undefined and exactly the same
    } else if (!(x instanceof Object) || !(y instanceof Object)) {
      return false; // if they are not strictly equal, they both need to be Objects
    } else if (!root && x.hasOwnProperty('id') && y.hasOwnProperty('id')) {
      return x.id === y.id;
    } else if (x.constructor !== y.constructor) {
      // they must have the exact same prototype chain, the closest we can do is
      // test their constructor.
      return false;
    } else if (x instanceof DateTime && y instanceof DateTime) {
      // if is DateTime compare with Milliseconds to ignore locale
      return x.toMillis() === y.toMillis();
    } else if (x instanceof Date && y instanceof Date) {
      // if is Date compare with Milliseconds to ignore locale
      return x.getTime() === y.getTime();
    } else {
      for (const p in x) {
        if (!x.hasOwnProperty(p)) {
          continue; // other properties were tested using x.constructor === y.constructor
        }
        if (!y.hasOwnProperty(p)) {
          return false; // allows to compare x[ p ] and y[ p ] when set to undefined
        }
        if (x[p] === y[p]) {
          continue; // if they have the same strict value or identity then they are equal
        }
        if (typeof (x[p]) !== 'object') {
          return false; // Numbers, Strings, Functions, Booleans must be strictly equal
        }
        if (!this.deepEquals(x[p], y[p], false)) {
          return false;
        }
      }
      for (const p in y) {
        if (y.hasOwnProperty(p) && !x.hasOwnProperty(p)) {
          return false;
        }
      }
      return true;

    }
  }
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class ActionService implements IActionService, OnDestroy {

  private fragments = new Map<string, IFragment>();
  /**
   * Subscriptions managed by the service. Any registred subscription will be unsubscribed on destroy
   */
  protected subscriptions: Subscription[] = [];
  private edit = false;
  private masterFragmentName: string;
  private actionLinks: ActionLink[] = [];

  status$: BehaviorSubject<ActionStatus> = new BehaviorSubject<ActionStatus>({ changes: false, complete: false, errors: false, problems: false, edit: false });

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      return from(this.fragments.values()).pipe(
        filter((part) => part.hasChanges()),
        tap((part) => part.clearProblems()),
        mergeMap((part) => part.saveOrUpdate().pipe(
          catchError(error => {
            if (error instanceof HttpProblem) {
              part.pushProblems(error);
              error.managed = true;
            }
            return throwError(error);
          }),
          switchMap(() => {
            return of(void 0);
          }),
          tap(() => part.refreshInitialState(true)))
        ),
        takeLast(1),
        defaultIfEmpty(void 0)
      );
    }
    else {
      const part = this.fragments.get(this.masterFragmentName);
      part.clearProblems();
      return part.saveOrUpdate().pipe(
        catchError(error => {
          if (error instanceof HttpProblem) {
            part.pushProblems(error);
            error.managed = true;
          }
          return throwError(error);
        }),
        tap(() => part.refreshInitialState(true)),
        switchMap((key) => {
          if (typeof key === 'string' || typeof key === 'number') {
            this.onKeyChange(key);
          }
          return this.saveOrUpdate();
        }),
        takeLast(1)
      );
    }
  }

  /**
   * Callback invoked after master fragment saveOrUpdate call when creating
   * used to propagated the returned key to others fragment
   * @param value The new identity key
   */
  protected onKeyChange(value: string | number): void {
    this.fragments.forEach((fragment) => fragment.setKey(value));
  }

  public addFragment(name: string, fragment: IFragment) {
    if (this.fragments.size === 0) {
      this.masterFragmentName = name;
    }
    this.fragments.set(name, fragment);
    this.subscriptions.push(fragment.status$.subscribe((status) => {
      this.evalFragmentStatus(status);
    }));
  }

  /**
   * Set the name of the fragment that is considered master fragment during creation.
   * By default, the firstly registered part
   */
  protected set masterFragment(name: string) {
    this.masterFragmentName = name;
  }

  /**
   * Enable the edition mode in the action.
   * This do not affect the mode of the nested fragments
   */
  protected enableEdit(): void {
    this.edit = true;
    const current = this.status$.value;
    current.edit = true;
    this.status$.next(current);
  }

  /**
   * Returns true if edition mode is enabled
   */
  protected isEdit(): boolean {
    return this.edit;
  }

  private evalFragmentStatus(status: FragmentStatus): void {
    const current = this.status$.value;
    const errors = this.hasErrors();
    const changes = this.hasChanges();
    const problem = this.hasProblems();
    let complete = this.isComplete();
    let update = false;
    // If one element transit to edition, we too
    if (status.edit) {
      this.edit = true;
    }
    if (current.errors !== errors) {
      current.errors = errors;
      update = true;
    }
    if (current.problems !== problem) {
      current.problems = problem;
      update = true;
    }
    if (current.changes !== changes) {
      current.changes = changes;
      update = true;
    }
    if (!this.isEdit() && complete && !errors) {
      complete = true;
    }
    else {
      complete = false;
    }
    if (current.complete !== complete) {
      current.complete = complete;
      update = true;
    }
    if (current.edit !== this.edit) {
      current.edit = this.edit;
      update = true;
    }
    if (update) {
      this.status$.next(current);
    }
  }

  getFragment(name: string): IFragment {
    return this.fragments.get(name);
  }

  hasErrors(): boolean {
    let errors = false;
    this.fragments.forEach((fragment) => errors = errors || fragment.hasErrors());
    return errors;
  }

  hasProblems(): boolean {
    let problems = false;
    this.fragments.forEach((fragment) => problems = problems || fragment.hasProblems());
    return problems;
  }

  hasChanges(): boolean {
    let changes = false;
    this.fragments.forEach((fragment) => changes = changes || fragment.hasChanges());
    return changes;
  }

  private isComplete(): boolean {
    let uninitialized = false;
    this.fragments.forEach((fragment) => uninitialized = uninitialized || !fragment.isComplete());
    return !uninitialized;
  }

  performChecks(markAllTouched = false): void {
    this.fragments.forEach((fragment) => fragment.performChecks(markAllTouched));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.fragments.forEach((fragment) => fragment.destroy());
  }

  /**
   * Add a action link
   * @param value action link to add
   */
  protected addActionLink(value: ActionLink) {
    if (!value.titleParams) {
      value.titleParams = {};
    }
    this.actionLinks.push(value);
  }

  getActionLinks(): ActionLink[] {
    return this.actionLinks;
  }
}
