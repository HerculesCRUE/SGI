import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { AfterViewInit, ChangeDetectorRef, Directive, DoCheck, EventEmitter, Input, OnDestroy, Optional, Output, Self, ViewChild } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

let nextUniqueId = 0;

/** SelectValue used by the SGI select components */
export interface SelectValue<T> {
  /** Item used as value */
  item: T;
  /** Display text to be rendered for the option */
  displayText: string;
  /** Indicate if is a missing option */
  missing: boolean;
  /** Indicate if the option is selectable */
  disabled: boolean;
}

/** Base class that provide common functionality to SGI select */
@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class SelectCommonComponent<T>
  implements ControlValueAccessor, MatFormFieldControl<T>, AfterViewInit, DoCheck, OnDestroy {

  /** Unique id for this input. */
  // tslint:disable-next-line: variable-name
  private _uid = `sgi-select-${nextUniqueId++}`;

  /** True when the component is initialized and ready  */
  protected ready = false;
  /** True when the options has been provided througth options attribute */
  protected externalData = false;
  /** Store subscriptions to be unsuscribed onDestroy */
  protected subscriptions: Subscription[] = [];
  /** Embedded MatSelect */
  @ViewChild(MatSelect) protected readonly matSelect: MatSelect;
  readonly stateChanges = new Subject<void>();
  readonly controlType = 'sgi-select';

  get tabIndex(): number { return -1; }

  /** Value of the select control. */
  @Input()
  get value(): T {
    return this.ready ? this.matSelect.value : this._value;
  }
  set value(newValue: T) {
    if (this.ready) {
      this.matSelect.value = newValue;
    }
    else {
      this._value = newValue;
    }
  }
  // tslint:disable-next-line: variable-name
  private _value: T;

  /** Unique id of the element. */
  @Input()
  get id(): string {
    return this._id;
  }
  set id(value: string) {
    this._id = value || this._uid;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  /** Placeholder to be shown if no value has been selected. */
  @Input()
  get placeholder(): string {
    return this.ready ? this.matSelect.placeholder : this._placeholder;
  }
  set placeholder(value: string) {
    if (this.ready) {
      this.matSelect.placeholder = value;
    }
    else {
      this._placeholder = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _placeholder: string;

  /** Whether the select is focused. */
  get focused(): boolean {
    return this.matSelect?.focused ?? false;
  }

  get empty(): boolean {
    return this.matSelect?.empty ?? true;
  }

  get shouldLabelFloat(): boolean {
    return this.matSelect?.shouldLabelFloat ?? false;
  }

  /** Whether the component is required. */
  @Input()
  get required(): boolean {
    return this.ready ? this.matSelect.required : this._required;
  }
  set required(value: boolean) {
    if (this.ready) {
      this.matSelect.required = value;
      this.stateChanges.next();
    }
    else {
      this._required = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  @Input()
  get disabled(): boolean {
    return this.ready ? this.matSelect.disabled : this._disabled;
  }
  set disabled(value: boolean) {
    if (this.ready) {
      this.matSelect.disabled = value;
    }
    else {
      this._disabled = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  /** Whether the component is in an error state. */
  get errorState(): boolean {
    return this.ready ? this.matSelect.errorState : this._errorState;
  }
  set errorState(value: boolean) {
    if (this.ready && this.matSelect.errorState !== value) {
      this.matSelect.errorState = value;
      this._errorState = value;
      this.stateChanges.next();
    }
    else if (this._errorState !== value) {
      this._errorState = value;
      this.stateChanges.next();
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorState = false;

  /** Object used to control when error messages are shown. */
  @Input()
  set errorStateMatcher(errorStateMatcher: ErrorStateMatcher) {
    if (this.ready) {
      this.matSelect.errorStateMatcher = errorStateMatcher;
    }
    else {
      this._errorStateMatcher = errorStateMatcher;
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorStateMatcher: ErrorStateMatcher;

  /**
   * Options to render
   */
  @Input()
  get options(): T[] {
    return this._options;
  }
  set options(value: T[]) {
    this.externalData = true;
    const newValue = value ? value : [];

    const changes = !(this._options.length === newValue.length && this._options.every((v, i) => v === newValue[i]));
    this._options = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _options: T[] = [];

  /** Display a  missing option. Default: true */
  @Input()
  get showMissing(): boolean {
    return this._showMissing;
  }
  set showMissing(value: boolean) {
    this._showMissing = coerceBooleanProperty(value);
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _showMissing = true;

  /** Select Values */
  get selectValues(): SelectValue<T>[] { return this._selectValues; }
  set selectValues(newValue: SelectValue<T>[]) {
    this._selectValues = newValue;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _selectValues: SelectValue<T>[] = [];

  /** Observable that trigger a reset, clearing the selected option and value when emmit any value */
  @Input()
  set resetOnChange(value: Observable<any>) {
    if (value) {
      this._resetOnChange = value;
      this.subscriptions.push(this._resetOnChange.subscribe(
        () => {
          this.ngControl.control.markAsTouched({ onlySelf: true });
          this.resetSelection();
        }
      ));
    }
  }
  // tslint:disable-next-line: variable-name
  private _resetOnChange: Observable<any>;

  /** Emitter fo the user selection */
  @Output()
  readonly selectionChange: EventEmitter<T> = new EventEmitter<T>();

  /**
   * Function to compare the option values with the selected values. The first argument
   * is a value from an option. The second is a value from the selection. A boolean
   * should be returned.
   */
  @Input()
  get compareWith() {
    return this.ready ? this.matSelect.compareWith : this._compareWith;
  }
  set compareWith(fn: (o1: T, o2: T) => boolean) {
    if (this.ready) {
      this.matSelect.compareWith = fn;
    }
    else {
      this._compareWith = fn;
    }
  }
  // tslint:disable-next-line: variable-name
  private _compareWith = (o1: T, o2: T) => o1 === o2;

  /**
   * Function to sort the options.
   */
  @Input()
  get sortWith() {
    return this._sortWith;
  }
  set sortWith(fn: (o1: SelectValue<T>, o2: SelectValue<T>) => number) {
    this._sortWith = fn;
  }
  // tslint:disable-next-line: variable-name
  private _sortWith: (o1: SelectValue<T>, o2: SelectValue<T>) => number = (o1, o2) => 0;

  /**
   * Function to resolve the display text of the options.
   */
  @Input()
  get displayWith() {
    return this._displayWith;
  }
  set displayWith(fn: (option: T) => string) {
    this._displayWith = fn;
    if (this.ready) {
      this.refreshDisplayValue();
    }
  }
  // tslint:disable-next-line: variable-name
  private _displayWith: (option: T) => string = (option) => `${option}`;

  /**
   * Function to resolve if the option should be disabled.
   */
  @Input()
  get disableWith() {
    return this._disableWith;
  }
  set disableWith(fn: (option: T) => boolean) {
    this._disableWith = fn;
    if (this.ready) {
      this.refreshDisableValue();
    }
  }
  // tslint:disable-next-line: variable-name
  private _disableWith: (option: T) => boolean = () => false;

  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };

  /** `View -> model callback called when select has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  constructor(
    // tslint:disable-next-line: variable-name
    private _defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() public ngControl: NgControl) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngDoCheck(): void {
    if (this.ready) {
      // Call ngDoCheck to evaluate errorState
      this.matSelect.ngDoCheck();
      this.errorState = this.matSelect.errorState;
    }
  }

  ngAfterViewInit(): void {
    this.matSelect.ngControl = this.ngControl;
    this.subscriptions.push(this.matSelect.selectionChange.subscribe(
      (event: MatSelectChange) => this.selectionChange.next(event.value)
    ));

    // Defer setting the value in order to avoid the "Expression
    // has changed after it was checked" errors from Angular.
    Promise.resolve().then(() => {
      this.matSelect.required = this._required;
      this.matSelect.disabled = this._disabled;
      this.matSelect.placeholder = this._placeholder;
      this.matSelect.errorStateMatcher = this._errorStateMatcher || this._defaultErrorStateMatcher;
      this.matSelect.errorState = this._errorState;
      this.matSelect.value = this._value;
      this.matSelect.registerOnChange(this._onChange);
      this.matSelect.registerOnTouched(this._onTouched as () => {});
      // The asignation of a comparer trigger update of MatSelect
      this.matSelect.compareWith = this._compareWith;
      this.ready = true;
      this.stateChanges.next();

      this.loadData();
    });
  }

  setDescribedByIds(ids: string[]): void {
    this.matSelect?.setDescribedByIds(ids);
  }

  onContainerClick(event: MouseEvent): void {
    this.matSelect.onContainerClick();
  }

  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: any): void {
    if (this.ready) {
      this.matSelect.registerOnChange(fn);
    }
    else {
      this._onChange = fn;
    }
  }

  registerOnTouched(fn: () => {}): void {
    if (this.ready) {
      this.matSelect.registerOnTouched(fn);
    }
    else {
      this._onTouched = fn;
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  private toSelectValue(options: T[]): SelectValue<T>[] {
    return options.map(option => {
      return {
        item: option,
        displayText: this.displayWith(option),
        missing: false,
        disabled: this.disableWith(option)
      };
    });
  }

  private refreshDisplayValue(): void {
    this.selectValues.forEach((value => value.displayText = this.displayWith(value.item)));
    this.selectValues.sort(this.sortWith);
  }

  private refreshDisableValue(): void {
    this.selectValues.forEach((value => value.disabled = value.missing ? false : this.disableWith(value.item)));
  }

  /** Reset the selected value to null */
  protected resetSelection(): void {
    this.value = null;
    this._onChange(null);
    this.selectionChange.next(null);
  }

  private addMissingOptionIfNeccesary(): void {
    if (this.showMissing) {
      const value = this.value;
      if (value) {
        const exists = this.selectValues.some(op => this.compareWith(op.item, value));
        if (!exists) {
          const missingValue: SelectValue<T> = {
            item: value,
            displayText: this.displayWith(value),
            missing: true,
            disabled: false
          };
          const insertIndex = this.getInsertIndex(this.selectValues, missingValue);
          this.selectValues.splice(insertIndex, 0, missingValue);
          this.selectValues.sort(this.sortWith);
        }
      }
    }
  }

  private getInsertIndex(options: SelectValue<T>[], value: SelectValue<T>): number {
    if (!options.length) {
      return 0;
    }
    else {
      if (value.displayText.localeCompare(options[options.length - 1].displayText) >= 0) {
        return options.length;
      }
      return options.findIndex(option => (value.displayText.localeCompare(option.displayText) <= 0));
    }
  }

  /**
   * Load the options to be rendered.
   *
   * If the currente value is missing from the options and showMissingOption is true, then the value will be added.
   */
  protected loadData(): void {
    this.loadOptions().pipe(
      map(options => {
        return this.toSelectValue(options);
      })
    ).subscribe((options) => {
      this.selectValues = options;
      this.selectValues.sort(this.sortWith);

      this.addMissingOptionIfNeccesary();

      if (this.ready) {
        // Hack to trigger refresh
        this.matSelect.setDisabledState(this.disabled);
      }
      this.stateChanges.next();
    });
  }

  /**
   * Load the options that should be rendered when loadData() is called.
   */
  protected loadOptions(): Observable<T[]> {
    return of(this.options);
  }

}
