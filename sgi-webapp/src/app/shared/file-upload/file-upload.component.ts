import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Attribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, DoCheck, ElementRef, EventEmitter, Inject, Input, OnDestroy, OnInit, Optional, Output, Self, ViewChild } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { Observable, of, Subject, throwError } from 'rxjs';
import {
  BACKSPACE,
  DELETE,
  ENTER,
  hasModifierKey,
  SPACE,
} from '@angular/cdk/keycodes';
import { IDocumento } from '@core/models/sgdoc/documento';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { catchError, filter, map, takeLast, tap } from 'rxjs/operators';
import { HttpEventType } from '@angular/common/http';

let nextUniqueId = 0;


export interface UploadEvent {
  status: 'start' | 'end' | 'error' | 'progress';
  progress?: number;
}

@Component({
  selector: 'sgi-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple', 'tabIndex'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    'role': 'search',
    'aria-autocomplete': 'none',
    'class': 'mat-select',
    '[attr.id]': 'id',
    '[attr.tabindex]': 'tabIndex',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '[class.mat-select-disabled]': 'disabled',
    '[class.mat-select-invalid]': 'errorState',
    '[class.mat-select-required]': 'required',
    '[class.mat-select-empty]': 'empty',
    '(keydown)': 'handleKeydown($event)',
    '(focus)': 'onFocus()',
    '(blur)': 'onBlur()',
  },
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SgiFileUploadComponent
    }
  ],
})
export class SgiFileUploadComponent implements
  OnDestroy, OnInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<IDocumento> {

  /** Unique id for this input. */
  private uid = `sgi-file-upload-${nextUniqueId++}`;

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
  controlType = 'sgi-file-upload';

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
  get value(): IDocumento { return this._value; }
  set value(newValue: IDocumento) {
    if (newValue !== this._value) {
      this._value = newValue;
    }
  }
  // tslint:disable-next-line: variable-name
  private _value: IDocumento;

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

  uploading = false;

  private selection: File;

  get displayValue(): string {
    if (this.empty) {
      return '';
    }

    return `${this.value.nombre}`;
  }

  @Input()
  get accept(): string { return this._accept; }
  set accept(value: string) {
    this._accept = value;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _accept: string;

  @Output()
  readonly uploadEventChange: EventEmitter<UploadEvent> = new EventEmitter<UploadEvent>();

  @Output() readonly uploadStart: Observable<void> = this.uploadEventChange.pipe(filter(e => e.status === 'start'), map(() => { }));
  @Output() readonly uploadEnd: Observable<void> = this.uploadEventChange.pipe(filter(e => e.status === 'end'), map(() => { }));
  @Output() readonly uploadError: Observable<void> = this.uploadEventChange.pipe(filter(e => e.status === 'error'), map(() => { }));
  @Output() readonly uploadProgress: Observable<number> = this.uploadEventChange.pipe(filter(e => e.status === 'progress'), map((e) => e.progress));

  @Output()
  readonly selectionChange: EventEmitter<File> = new EventEmitter<File>();

  @Input()
  get autoUpload(): boolean { return this._autoUpload; }
  set autoUpload(value: boolean) {
    this._autoUpload = coerceBooleanProperty(value);
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _autoUpload = true;

  @Input()
  get showProgress(): boolean { return this._showProgress; }
  set showProgress(value: boolean) {
    this._showProgress = coerceBooleanProperty(value);
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _showProgress = true;

  @ViewChild('fileUpload') private fileUpload: ElementRef;

  private onChange: (value: any) => void = () => { };
  private onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) private parentFormField: MatFormField,
    @Self() @Optional() public ngControl: NgControl,
    @Attribute('tabindex') tabIndex: string,
    private documentoService: DocumentoService) {

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
        this.triggerUpload();
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
    if (this.focused || (this.showProgress && this.uploading)) {
      return true;
    }
    return !this.empty;
  }

  triggerUpload(): void {
    if (!this.disabled) {
      this.dialogOpen = true;
      this.fileUpload.nativeElement.click();
      this.changeDetectorRef.markForCheck();
    }
  }

  onSeletectFile(files: FileList) {
    this.dialogOpen = false;
    this.changeDetectorRef.markForCheck();
    if (files && files.length) {
      this.selection = files.item(0);
      this.selectionChange.next(this.selection);
      if (this.autoUpload) {
        this.uploadSelection().subscribe();
      }
      else {
        this._value = {
          nombre: this.selection.name,
          tipo: this.selection.type
        } as IDocumento;
        this.propagateChanges();
      }
    }
    else {
      this.selection = null;
      this.selectionChange.next(this.selection);
    }
  }

  uploadSelection(): Observable<void> {
    if (this.selection && !this.disabled) {
      this.uploading = true;
      return this.documentoService.uploadFicheroWithStatus(this.selection).pipe(
        map(event => {
          switch (event.type) {
            case HttpEventType.Sent:
              this.uploadEventChange.next({ status: 'start' });
              break;
            case HttpEventType.UploadProgress:
              this.uploadEventChange.next({ status: 'progress', progress: Math.round(100 * event.loaded / event.total) });
              break;
            case HttpEventType.Response:
              this._value = event.body;
              this.fileUpload.nativeElement.value = null;
              this.uploading = false;
              this.selection = null;
              this.uploadEventChange.next({ status: 'end' });
              break;
          }
          this.propagateChanges();
          return event;
        }),
        takeLast(1),
        map(() => void 0),
        catchError(error => {
          this._value = null;
          this.uploading = false;
          this.fileUpload.nativeElement.value = null;
          this.uploadEventChange.next({ status: 'error' });
          this.propagateChanges();
          return throwError(error);
        })
      );
    }
    else {
      return of(void 0);
    }
  }
}
