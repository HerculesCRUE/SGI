import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DoCheck,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Self,
  ViewChild
} from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { Subject, Subscription } from 'rxjs';

let nextUniqueId = 0;

/**
 * A regular expression that matches valid e-mail addresses.
 *
 * At a high level, this regexp matches e-mail addresses of the format `local-part@tld`, where:
 * - `local-part` consists of one or more of the allowed characters (alphanumeric and some
 *   punctuation symbols).
 * - `local-part` cannot begin or end with a period (`.`).
 * - `local-part` cannot be longer than 64 characters.
 * - `tld` consists of one or more `labels` separated by periods (`.`). For example `localhost` or
 *   `foo.com`.
 * - A `label` consists of one or more of the allowed characters (alphanumeric, dashes (`-`) and
 *   periods (`.`)).
 * - A `label` cannot begin or end with a dash (`-`) or a period (`.`).
 * - A `label` cannot be longer than 63 characters.
 * - The whole address cannot be longer than 254 characters.
 *
 * ## Implementation background
 *
 * This regexp was ported over from AngularJS (see there for git history):
 * https://github.com/angular/angular.js/blob/c133ef836/src/ng/directive/input.js#L27
 * It is based on the
 * [WHATWG version](https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address) with
 * some enhancements to incorporate more RFC rules (such as rules related to domain names and the
 * lengths of different parts of the address). The main differences from the WHATWG version are:
 *   - Disallow `local-part` to begin or end with a period (`.`).
 *   - Disallow `local-part` length to exceed 64 characters.
 *   - Disallow total address length to exceed 254 characters.
 *
 * See [this commit](https://github.com/angular/angular.js/commit/f3f5cf72e) for more details.
 */
const EMAIL_REGEXP =
  /^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;

@Component({
  selector: 'sgi-input-emails',
  templateUrl: './input-emails.component.html',
  styleUrls: ['./input-emails.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: InputEmailsComponent
    }
  ]
})
export class InputEmailsComponent implements
  OnDestroy, OnInit, AfterViewInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<string[]> {

  /** Input control */
  readonly emailsInputCtrl = new FormControl();


  /** Number of elements resolved in the service */
  get filterElements(): number {
    return this._filterElements;
  }
  // tslint:disable-next-line: variable-name
  private _filterElements = 0;

  /** Number of total elements that match the input */
  get totalElements(): number {
    return this._totalElements;
  }
  // tslint:disable-next-line: variable-name
  private _totalElements = 0;

  /** True when the component is initialized and ready  */
  private ready = false;
  /** Store subscriptions to be unsuscribed onDestroy */
  private subscriptions: Subscription[] = [];

  /** Embedded chipList of selected keywords */
  @ViewChild('chipList') private readonly chipList!: MatChipList;
  /** Embedded input for autocomplete */
  // @ViewChild('emailsInput') private readonly palabraInput!: ElementRef<HTMLInputElement>; ---------------------------------------------------------- 

  /** Unique id for this input. */
  private uid = `sgi-input-emails-${nextUniqueId++}`;

  // tslint:disable-next-line: variable-name
  _valueId = `sgi-input-emails-value-${nextUniqueId++}`;

  /** Whether the component is focused. */
  get focused(): boolean {
    return this.chipList?.focused ?? false;
  }

  /** A name for this control that can be used by `mat-form-field`. */
  controlType = 'sgi-input-emails';

  /** Placeholder to be shown if no value has been selected. */
  @Input()
  get placeholder(): string {
    return this.ready ? this.chipList.placeholder : this._placeholder;
  }
  set placeholder(value: string) {
    if (this.ready) {
      this.chipList.placeholder = value;
    }
    else {
      this._placeholder = value;
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _placeholder: string;

  /** Whether the component is required. */
  @Input()
  get required(): boolean {
    return this.ready ? this.chipList.required : this._required;
  }
  set required(value: boolean) {
    if (this.ready) {
      this.chipList.required = value;
      this.stateChanges.next();
    }
    else {
      this._required = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  /** Value of the component. */
  @Input()
  get value(): string[] {
    return this._value;
  }
  set value(newValue: string[]) {
    if (newValue === null) {
      this._value = [];
    }
    else {
      this._value = [...newValue];
    }

  }
  // tslint:disable-next-line: variable-name
  private _value: string[];

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  @Input()
  get disabled(): boolean {
    return this.ready ? this.chipList.disabled : this._disabled;
  }
  set disabled(value: boolean) {
    if (this.ready) {
      this.chipList.disabled = value;
    }
    else {
      this._disabled = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  get tabIndex(): number { return -1; }

  readonly stateChanges: Subject<void> = new Subject<void>();

  /** Whether the component is in an error state. */
  get errorState(): boolean {
    return this.ready ? this.chipList.errorState : this._errorState;
  }
  set errorState(value: boolean) {
    if (this.ready && this.chipList.errorState !== value) {
      this.chipList.errorState = value;
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
      this.chipList.errorStateMatcher = errorStateMatcher;
    }
    else {
      this._errorStateMatcher = errorStateMatcher;
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorStateMatcher: ErrorStateMatcher;

  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };
  /** `View -> model callback called when component has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() public ngControl: NgControl
  ) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }
  }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.chipList.ngControl = this.ngControl;
    // Defer setting the value in order to avoid the "Expression
    // has changed after it was checked" errors from Angular.
    Promise.resolve().then(() => {
      this.chipList.required = this._required;
      this.chipList.disabled = this._disabled;
      this.chipList.placeholder = this._placeholder;
      this.chipList.errorStateMatcher = this._errorStateMatcher || this.defaultErrorStateMatcher;
      this.chipList.errorState = this._errorState;
      this.chipList.registerOnChange(this._onChange);
      this.chipList.registerOnTouched(this._onTouched as () => {});
      this.ready = true;
      this.stateChanges.next();
    });
  }

  ngDoCheck(): void {
    if (this.ready) {
      // Call ngDoCheck to evaluate errorState
      this.chipList.ngDoCheck();
      this.errorState = this.chipList.errorState;
    }
  }

  _add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value?.toLocaleLowerCase();

    if ((value || '').trim()) {
      if (EMAIL_REGEXP.test(value)) {
        if (!this.value.some((element) => element === value)) {
          this.propagateChanges([...this.value, value]);
        }
      }
    }

    if (input) {
      input.value = '';
    }

    this.emailsInputCtrl.setValue(null);
  }

  _remove(fruit: string): void {
    const index = this.value.indexOf(fruit);

    if (index >= 0) {
      const current = [...this.value];
      current.splice(index, 1);
      this.propagateChanges(current);
    }
  }

  private propagateChanges(value: string[]) {
    this.value = value;
    this._onChange(this.value);
    this.changeDetectorRef.markForCheck();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  // Value Accesor ***************************
  writeValue(value: any): void {
    this.value = value;
    this.changeDetectorRef.markForCheck();
  }

  registerOnChange(fn: (value: any) => void): void {
    if (this.ready) {
      this.chipList.registerOnChange(fn);
    }
    else {
      this._onChange = fn;
    }
  }

  registerOnTouched(fn: () => {}): void {
    if (this.ready) {
      this.chipList.registerOnTouched(fn);
    }
    else {
      this._onTouched = fn;
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  /** Whether the component has a value. */
  get empty(): boolean {
    return (!this.emailsInputCtrl.value) && (!this.value || this.value.length === 0);
  }

  setDescribedByIds(ids: string[]) {
    this.chipList?.setDescribedByIds(ids);
  }

  onContainerClick(event: MouseEvent) {
    this.chipList.onContainerClick(event);
  }

  get shouldLabelFloat(): boolean {
    return !this.empty || this.focused;
  }

}
