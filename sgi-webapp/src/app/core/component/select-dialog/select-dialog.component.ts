import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  BACKSPACE,
  DELETE,
  ENTER,
  hasModifierKey,
  SPACE
} from '@angular/cdk/keycodes';
import { ComponentType } from '@angular/cdk/portal';
import { ChangeDetectorRef, Directive, DoCheck, ElementRef, Inject, Input, OnDestroy, OnInit, Optional, Self, TemplateRef, ViewChild } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, startWith, switchMap, takeUntil } from 'rxjs/operators';

let nextUniqueId = 0;

export interface SearchModalData {
  searchTerm: string;
  extended: boolean;
}

export interface SearchResult<T> {
  items: T[];
  more: boolean;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class SelectDialogComponent<D, T> implements
  OnDestroy, OnInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<T> {

  /** Input control for the autocomplete */
  readonly searchInputCtrl = new FormControl();
  /** Result of elements to display in the autocomplete */
  readonly searchResult$: Subject<T[]> = new BehaviorSubject<T[]>([]);

  /** Emits whenever the component is destroyed. */
  // tslint:disable-next-line: variable-name
  private readonly _destroy = new Subject<void>();

  /** Embedded input for autocomplete */
  @ViewChild('searchInput')
  set searchInput(element: ElementRef<HTMLInputElement>) {
    this._searchInput = element;
    if (this.focused && element) {
      this._searchInput.nativeElement.focus();
    }
  }
  get searchInput(): ElementRef<HTMLInputElement> {
    return this._searchInput;
  }
  // tslint:disable-next-line: variable-name
  private _searchInput: ElementRef<HTMLInputElement>;

  /** Embedded input for display selected value */
  @ViewChild('displayInput')
  set displayInput(element: ElementRef<HTMLInputElement>) {
    this._displayInput = element;
    if (this.focused && element) {
      this._displayInput.nativeElement.focus();
    }
  }
  get displayInput(): ElementRef<HTMLInputElement> {
    return this._displayInput;
  }
  // tslint:disable-next-line: variable-name
  private _displayInput: ElementRef<HTMLInputElement>;;
  @ViewChild('auto') private readonly autocomplete!: MatAutocomplete;

  /** Unique id for this input. */
  private uid = `sgi-select-dialog-${nextUniqueId++}`;

  // tslint:disable-next-line: variable-name
  _valueId = `sgi-select-dialog-value-${nextUniqueId++}`;

  /** Whether the select is focused. */
  get focused(): boolean {
    return this._focused;
  }
  // tslint:disable-next-line: variable-name
  private _focused = false;

  /** A name for this control that can be used by `mat-form-field`. */
  controlType = 'sgi-select-dialog';

  /** Placeholder to be shown if no value has been selected. */
  @Input()
  get placeholder(): string {
    return this._placeholder ?? '';
  }
  set placeholder(value: string) {
    this._placeholder = value;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _placeholder: string;

  /** Whether the component is required. */
  @Input()
  get required(): boolean { return this._required; }
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  /** Value of the select control. */
  @Input()
  get value(): T { return this._value; }
  set value(newValue: T) {
    if (newValue !== this._value) {
      this._value = newValue;
    }
  }
  // tslint:disable-next-line: variable-name
  private _value: T;

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  @Input()
  get extended(): boolean {
    return this._extended;
  }
  set extended(value: boolean) {
    this._extended = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _extended = true;

  private dialogOpen = false;

  get tooManyResults(): boolean {
    return this._tooManyResults;
  }
  // tslint:disable-next-line: variable-name
  private _tooManyResults = false;

  get disabled(): boolean {
    return this._disabled;
  }
  set disabled(disabled: boolean) {
    this._disabled = disabled;
    if (disabled) {
      this.searchInputCtrl.disable();
    }
    else {
      this.searchInputCtrl.enable();
    }
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  get tabIndex(): number {
    return -1;
  }
  autofilled?: boolean;

  readonly stateChanges: Subject<void> = new Subject<void>();
  errorState: boolean;
  disableRipple: boolean;

  @Input()
  get displayWith() {
    return this._displayWith;
  }
  set displayWith(fn: (option: T) => string) {
    this._displayWith = fn;
  }
  // tslint:disable-next-line: variable-name
  private _displayWith: (option: T) => string = (option) => `${option}`;

  get notFoundSelectedValue$(): Observable<boolean> {
    return this._notFoundSelectedValue$.asObservable();
  }
  protected setNotFoundSelectedValue(value: boolean) {
    this._notFoundSelectedValue$.next(value);
  }
  // tslint:disable-next-line: variable-name
  private _notFoundSelectedValue$ = new BehaviorSubject<boolean>(false);

  private onChange: (value: any) => void = () => { };
  private onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) private parentFormField: MatFormField,
    @Self() @Optional() public ngControl: NgControl,
    private dialog: MatDialog,
    private dialogToShow: ComponentType<D> | TemplateRef<D>,
    private focusMonitor: FocusMonitor
  ) {

    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }

    // Force setter to be called in case id was not specified.
    this.id = this.id;
  }

  ngOnInit() {
    this.searchInputCtrl.valueChanges.pipe(
      takeUntil(this._destroy),
      startWith(''),
      debounceTime(200),
      switchMap(value => this.searchFilter(value)),
    ).subscribe(
      (response => {
        this.searchResult$.next(response.items);
        this._tooManyResults = response.more;
        this.stateChanges.next();
      })
    );

    this.focusMonitor.monitor(this.elementRef, true)
      .pipe(
        takeUntil(this._destroy)
      ).subscribe(
        (value) => {
          if (!!value && !this.disabled) {
            this._focused = !!value;
            this.stateChanges.next();
          }
          if (!!!value) {
            this._focused = !!value;
            if (!this.disabled && !this.dialogOpen) {
              this.onTouched();
              this.changeDetectorRef.markForCheck();
              this.stateChanges.next();
            }
            if (this.searchInputCtrl.value) {
              this.clearSearch();
            }
          }
        }
      );
  }

  private searchFilter(term: string): Observable<SearchResult<T>> {
    if (term?.trim()?.length >= 3) {
      return this.search(term.trim()).pipe(
        catchError(() => {
          return of({ items: [], more: false } as SearchResult<T>);
        })
      );
    }
    else {
      return of({ items: [], more: false } as SearchResult<T>);
    }
  }

  protected abstract search(term: string): Observable<SearchResult<T>>;

  ngDoCheck() {
    this.errorState = Boolean(this.ngControl) && !this.dialogOpen && (this.ngControl.touched && Boolean(this.ngControl.errors));
  }

  ngOnDestroy() {
    this.stateChanges.complete();
    this._destroy.next();
    this.focusMonitor.stopMonitoring(this.elementRef);
  }

  protected getDialogData(): SearchModalData {
    return {
      searchTerm: this.empty ? this.searchInputCtrl.value : '',
      extended: this.extended ?? false
    };
  }

  showDialog(): void {
    if (!this.disabled) {
      this.dialogOpen = true;
      const dialogRef = this.dialog.open<D, SearchModalData>(this.dialogToShow, {
        data: this.getDialogData(),
        minWidth: '70vw'
      });
      if (this.empty) {
        this.clearSearch();
      }

      dialogRef.afterOpened().subscribe(() => {
        this.changeDetectorRef.markForCheck();
        this.onTouched();
      });

      dialogRef.afterClosed().subscribe((returnValue: T) => {
        this.dialogOpen = false;
        if (this.empty) {
          this.searchInput.nativeElement.focus();
        }
        else {
          this.displayInput.nativeElement.focus();
        }
        if (returnValue) {
          this.propagateChanges(returnValue);
        }
      });
    }
  }

  // Value Accesor ***************************
  writeValue(value: any): void {
    this.value = value;
    this.changeDetectorRef.markForCheck();
  }

  registerOnChange(fn: (value: any) => void): void {
    this.onChange = fn;
  }


  registerOnTouched(fn: () => {}): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    this.changeDetectorRef.markForCheck();
    this.stateChanges.next();
  }

  // **************************************

  /** Handles all keydown events. */
  protected handleKeydown(event: KeyboardEvent): void {
    if (!this.disabled && !this.empty && this.focused) {
      const keyCode = event.keyCode;

      if ((keyCode === ENTER || keyCode === SPACE) && !hasModifierKey(event) && !event.defaultPrevented) {
        event.preventDefault();
        this.showDialog();
      } else if ((keyCode === BACKSPACE || keyCode === DELETE) && !hasModifierKey(event)) {
        event.preventDefault();
        if (this._value) {
          this.propagateChanges(null);
        }
      }
    }
  }

  /** Whether the component has a value. */
  get empty(): boolean {
    return !this.value;
  }

  /** Emits change event to set the model value. */
  private propagateChanges(value: T): void {
    this._value = value;
    this.onChange(value);
    this.stateChanges.next();
    this.changeDetectorRef.markForCheck();
  }

  setDescribedByIds(ids: string[]) {
    if (ids.length) {
      this.elementRef.nativeElement.setAttribute('aria-describedby', ids.join(' '));
    } else {
      this.elementRef.nativeElement.removeAttribute('aria-describedby');
    }
  }

  onContainerClick() {
    // Do nothing, monitored by focusMonitor
  }

  get shouldLabelFloat(): boolean {
    if (this.focused || this.autocomplete?.isOpen || this.searchInputCtrl.value) {
      return true;
    }
    return !this.empty;
  }

  _selected(event: MatAutocompleteSelectedEvent): void {
    this.propagateChanges(event.option.value);
    this.clearSearch();
  }

  private clearSearch(): void {
    this.searchInput.nativeElement.value = '';
    this.searchInputCtrl.setValue('');
  }
}
