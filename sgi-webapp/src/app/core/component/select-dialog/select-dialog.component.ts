import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Attribute, ChangeDetectorRef, DoCheck, ElementRef, Inject, Input, OnDestroy, OnInit, Optional, Self, TemplateRef, Directive } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { Subject } from 'rxjs';
import {
  BACKSPACE,
  DELETE,
  ENTER,
  hasModifierKey,
  SPACE,
} from '@angular/cdk/keycodes';
import { MatDialog } from '@angular/material/dialog';
import { ComponentType } from '@angular/cdk/portal';

let nextUniqueId = 0;

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class SelectDialogComponent<D, T> implements
  OnDestroy, OnInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<T> {

  /** Unique id for this input. */
  private uid = `sgi-select-dialog-${nextUniqueId++}`;

  /** Current `ariar-labelledby` value for the select trigger. */
  private triggerAriaLabelledBy: string | null = null;

  /** The aria-describedby attribute on the select for improved a11y. */
  protected ariaDescribedby: string;

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
  get placeholder(): string { return this._placeholder; }
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

  /** Aria label of the select. If not specified, the placeholder will be used as label. */
  @Input('aria-label') ariaLabel = '';

  /** Input that can be used to specify the `aria-labelledby` attribute. */
  @Input('aria-labelledby') ariaLabelledby: string;

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  private dialogOpen = false;

  disabled = false;
  tabIndex: number;
  autofilled?: boolean;

  stateChanges: Subject<void> = new Subject<void>();
  errorState: boolean;
  disableRipple: boolean;

  abstract readonly displayValue: string;

  private onChange: (value: any) => void = () => { };
  private onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) private parentFormField: MatFormField,
    @Self() @Optional() public ngControl: NgControl,
    @Attribute('tabindex') tabIndex: string,
    private dialog: MatDialog,
    private dialogToShow: ComponentType<D> | TemplateRef<D>) {

    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }

    this.tabIndex = parseInt(tabIndex, 10) || 0;

    // Force setter to be called in case id was not specified.
    this.id = this.id;
  }

  ngOnInit() {
    this.stateChanges.next();
  }

  ngDoCheck() {
    const newAriaLabelledby = this.getTriggerAriaLabelledby();

    // We have to manage setting the `aria-labelledby` ourselves, because part of its value
    // is computed as a result of a content query which can cause this binding to trigger a
    // "changed after checked" error.
    if (newAriaLabelledby !== this.triggerAriaLabelledBy) {
      const element: HTMLElement = this.elementRef.nativeElement;
      this.triggerAriaLabelledBy = newAriaLabelledby;
      if (newAriaLabelledby) {
        element.setAttribute('aria-labelledby', newAriaLabelledby);
      } else {
        element.removeAttribute('aria-labelledby');
      }
    }
    this.errorState = Boolean(this.ngControl) && !this.dialogOpen && (this.ngControl.touched && Boolean(this.ngControl.errors));
  }

  ngOnDestroy() {
    this.stateChanges.complete();
  }

  protected getDialogData(): any {
    return {};
  }

  showDialog(): void {
    if (!this.disabled) {
      this.dialogOpen = true;
      const dialogRef = this.dialog.open(this.dialogToShow, {
        panelClass: 'sgi-dialog-container',
        data: this.getDialogData()
      });

      dialogRef.afterOpened().subscribe(() => {
        this.changeDetectorRef.markForCheck();
        this.onTouched();
      });

      dialogRef.afterClosed().subscribe((returnValue: T) => {
        this.dialogOpen = false;
        if (returnValue) {
          this._value = returnValue;
          this.propagateChanges();
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

  /** Handles all keydown events on the select. */
  protected handleKeydown(event: KeyboardEvent): void {
    if (!this.disabled) {
      const keyCode = event.keyCode;

      if ((keyCode === ENTER || keyCode === SPACE) && !hasModifierKey(event)) {
        event.preventDefault();
        this.showDialog();
      } else if ((keyCode === BACKSPACE || keyCode === DELETE) && !hasModifierKey(event)) {
        event.preventDefault();
        if (this._value) {
          this._value = null;
          this.propagateChanges();
        }
      }
    }
  }

  protected onFocus() {
    if (!this.disabled) {
      this._focused = true;
      this.stateChanges.next();
    }
  }

  protected onBlur() {
    this._focused = false;

    if (!this.disabled && !this.dialogOpen) {
      this.onTouched();
      this.changeDetectorRef.markForCheck();
      this.stateChanges.next();
    }
  }

  /** Whether the select has a value. */
  get empty(): boolean {
    return !this.value;
  }

  /** Emits change event to set the model value. */
  private propagateChanges(): void {
    const valueToEmit = this.value;

    this._value = valueToEmit;
    this.onChange(valueToEmit);
    this.changeDetectorRef.markForCheck();
  }

  focus(options?: FocusOptions): void {
    this.elementRef.nativeElement.focus(options);
  }

  /** Gets the aria-labelledby for the select panel. */
  protected getPanelAriaLabelledby(): string | null {
    if (this.ariaLabel) {
      return null;
    }

    const labelId = this.getLabelId();
    return this.ariaLabelledby ? labelId + ' ' + this.ariaLabelledby : labelId;
  }

  /** Gets the ID of the element that is labelling the select. */
  private getLabelId(): string {
    return this.parentFormField?._labelId || '';
  }

  /** Gets the aria-labelledby of the select component trigger. */
  private getTriggerAriaLabelledby(): string | null {
    if (this.ariaLabel) {
      return null;
    }

    let value = this.getLabelId() + ' ' + this._valueId;

    if (this.ariaLabelledby) {
      value += ' ' + this.ariaLabelledby;
    }

    return value;
  }

  setDescribedByIds(ids: string[]) {
    this.ariaDescribedby = ids.join(' ');
  }

  onContainerClick() {
    this.focus();
  }

  get shouldLabelFloat(): boolean {
    if (this.focused && this.empty) {
      return true;
    }
    return !this.empty;
  }
}
