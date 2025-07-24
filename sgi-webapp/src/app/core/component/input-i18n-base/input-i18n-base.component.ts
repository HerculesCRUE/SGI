import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  ChangeDetectorRef,
  Directive,
  DoCheck,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Self
} from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Language } from '@core/i18n/language';
import { LanguageService } from '@core/services/language.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


@Directive()
export abstract class InputI18nBaseComponent
  implements ControlValueAccessor, MatFormFieldControl<(I18nFieldValue | I18nFieldValueResponse)[]>, OnInit, OnDestroy, DoCheck {

  protected abstract uid: string;

  readonly stateChanges: Subject<void> = new Subject<void>();
  readonly editValueChanges: Subject<string> = new Subject<string>();
  private hasBeenEditedByUser = false;
  private isPrioziationLocked = false;

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  @Input()
  get plainValue(): boolean {
    return this._plainValue;
  }
  set plainValue(value: boolean) {
    this._plainValue = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _plainValue = false;

  /** Value of the component. */
  @Input()
  get value(): (I18nFieldValue | I18nFieldValueResponse)[] {
    return this._value;
  }
  set value(newValue: (I18nFieldValue | I18nFieldValueResponse)[]) {
    if (newValue === null || !(newValue instanceof Array)) {
      this._value = [];
    }
    else {
      this._value = newValue;
    }
    this.editingValue = this.getCurrentEditingValue();

    if (!this.isPrioziationLocked && !this.hasBeenEditedByUser) {
      this.switchToPriorizedLanguage();
    }

    this.propagateChanges()
  }
  // tslint:disable-next-line: variable-name
  private _value: (I18nFieldValue | I18nFieldValueResponse)[] = [];

  /** Whether the component is required. */
  @Input()
  get required(): boolean {
    return this._required;
  }
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  @Input()
  get placeholder() {
    return this._placeholder;
  }
  set placeholder(plh) {
    this._placeholder = plh;
    this.stateChanges.next();
  }
  private _placeholder: string;


  @Input()
  get disabled(): boolean {
    return this._disabled;
  }
  set disabled(value: boolean) {
    this._disabled = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  @Input()
  get maxlength() {
    return this._maxlength;
  }
  set maxlength(value) {
    this._maxlength = value;
  }
  private _maxlength: string;

  // tslint:disable-next-line: variable-name
  /** Object used to control when error messages are shown. */
  @Input()
  set errorStateMatcher(errorStateMatcher: ErrorStateMatcher) {
    this._errorStateMatcher = errorStateMatcher;
  }
  get errorStateMatcher() {
    return this._errorStateMatcher;
  }
  // tslint:disable-next-line: variable-name
  private _errorStateMatcher: ErrorStateMatcher;

  get enabledLanguages(): Language[] {
    return this._enabledLanguages;
  }
  // tslint:disable-next-line: variable-name
  private _enabledLanguages = Language.values();

  set selectedLanguage(value: Language) {
    if (this._selectedLanguage != value) {
      this._selectedLanguage = value;
      this.isPrioziationLocked = true;
      this.editingValue = this.getCurrentEditingValue();
      this.isPrioziationLocked = false;
    }
  }
  get selectedLanguage(): Language {
    return this._selectedLanguage;
  }
  // tslint:disable-next-line: variable-name
  _selectedLanguage = null;

  set editingValue(value: string) {
    if (this._editingValue === value) {
      return;
    }
    this._editingValue = value;
    const currentI18nValue = this.getCurrentI18nFieldValue();
    const newI18nValue: I18nFieldValue | I18nFieldValueResponse = this.plainValue ? { value: value, lang: this.selectedLanguage.code } : { value: value, lang: this.selectedLanguage };
    const newValue = [...this.value];
    const idxCurrrentI18nValue = newValue.indexOf(currentI18nValue);
    if (value.length > 0) {
      if (!currentI18nValue) {
        newValue.push(newI18nValue);
      } else {

        newValue[idxCurrrentI18nValue] = newI18nValue;
      }
      this.value = newValue;
    } else if (currentI18nValue) {
      newValue.splice(idxCurrrentI18nValue, 1);
      this.value = newValue;
    } else {
      this.stateChanges.next();
    }
    this.editValueChanges.next(this.editingValue);
  }
  get editingValue(): string {
    return this._editingValue;
  }
  // tslint:disable-next-line: variable-name
  _editingValue = "";

  /** Whether the component is focused. */
  get focused(): boolean {
    return this._focused;
  }
  // tslint:disable-next-line: variable-name
  private _focused = false;

  get errorState(): boolean {
    return this._errorState;
  }
  set errorState(value: boolean) {
    if (this._errorState !== value) {
      this._errorState = value;
      this.stateChanges.next();
      this.changeDetectorRef.markForCheck();
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorState = false;

  get shouldLabelFloat(): boolean {
    return !this.empty || this.focused;
  }

  /** Whether the component has a value. */
  get empty(): boolean {
    return this.editingValue?.length <= 0;
  }

  get tabIndex(): number { return 1; }

  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };
  /** `View -> model callback called when component has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  @Input('aria-describedby') userAriaDescribedBy: string;

  protected readonly _destroy = new Subject<void>()

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    @Self() @Optional() public ngControl: NgControl,
    private defaultErrorStateMatcher: ErrorStateMatcher,
    private readonly languageService: LanguageService, // Subscribirse a los idiomas disponibles
    private focusMonitor: FocusMonitor
  ) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }
    this._enabledLanguages = this.languageService.getEnabledLanguages();
    this._selectedLanguage = this.languageService.getLanguage();
    this.languageService.languageChange$.pipe(takeUntil(this._destroy)).subscribe((lang) => {
      this.selectedLanguage = lang;
      this.switchToPriorizedLanguage();
    });

    this.errorStateMatcher = this._errorStateMatcher || this.defaultErrorStateMatcher;
  }

  ngOnInit(): void {
    this.focusMonitor.monitor(this.elementRef, true)
      .pipe(
        takeUntil(this._destroy)
      ).subscribe(
        (value) => {
          if (!this.disabled) {
            this._focused = !!value;
            if (!value) {
              this._onTouched();
            }
            this.stateChanges.next();
          }
        }
      );
  }

  private switchToPriorizedLanguage(): void {
    if (this.value?.length) {
      const priorizedValue = this.languageService.getField(this.value);
      if (priorizedValue && priorizedValue.lang.toString() !== this.selectedLanguage.code) {
        this.selectedLanguage = Language.fromCode(priorizedValue.lang.toString());
      }
    }
  }

  ngDoCheck(): void {
    if (this.ngControl) {
      this.updateErrorState();
    }
  }

  ngOnDestroy() {
    this._destroy.next();
  }

  private getCurrentI18nFieldValue(): I18nFieldValue | I18nFieldValueResponse {
    if (this.plainValue) {
      return this.value.find(v => v.lang === this.selectedLanguage.code);
    }
    else {
      return this.value.find(v => v.lang === this.selectedLanguage);
    }
  }

  private getCurrentEditingValue(): string {
    let i18nFieldValue = this.getCurrentI18nFieldValue();
    return i18nFieldValue?.value ?? '';
  }

  private propagateChanges() {
    this._onChange(this._value);
    this._onTouched();
    this.stateChanges.next();
  }

  private updateErrorState(): void {
    const matcher = this.errorStateMatcher || this.defaultErrorStateMatcher;
    const control = this.ngControl ? this.ngControl.control as FormControl : null;
    this.errorState = matcher.isErrorState(control, null);
  }

  // MatFormField interface ***************************
  onContainerClick(event: MouseEvent): void {
    // Do nothing, managed from focusMonitor
  }

  setDescribedByIds(ids: string[]) {
    if (ids.length) {
      this.elementRef.nativeElement.setAttribute('aria-describedby', ids.join(' '));
    } else {
      this.elementRef.nativeElement.removeAttribute('aria-describedby');
    }
  }

  // Value Accesor ***************************
  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: (value: any) => void): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: () => {}): void {
    this._onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  public onEdit(): void {
    this.hasBeenEditedByUser = true;
  }
  onInputFocus(): void {
    this.isPrioziationLocked = true;
  }

  onInputBlur(): void {
    this.isPrioziationLocked = false;
  }
}
