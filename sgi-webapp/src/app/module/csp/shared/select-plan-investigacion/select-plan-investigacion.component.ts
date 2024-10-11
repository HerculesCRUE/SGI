import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { BACKSPACE, DELETE, hasModifierKey } from '@angular/cdk/keycodes';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, Optional, Self } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { IPrograma } from '@core/models/csp/programa';
import { Subject } from 'rxjs';
import { SearchPlanInvestigacionModalComponent, SearchPlanInvestigacionModalData } from './dialog/search-plan-investigacion.component';

@Component({
  selector: 'sgi-select-plan-investigacion',
  templateUrl: './select-plan-investigacion.component.html',
  styleUrls: ['./select-plan-investigacion.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    'role': 'search',
    'aria-autocomplete': 'none',
    '[attr.id]': 'id',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '(keydown)': 'handleKeydown($event)'
  },
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectPlanInvestigacionComponent
    }
  ]
})
export class SelectPlanInvestigacionComponent implements MatFormFieldControl<IPrograma>, ControlValueAccessor, OnInit, OnDestroy {
  static nextId = 0;
  id = `input-button-${SelectPlanInvestigacionComponent.nextId++}`;
  stateChanges = new Subject<void>();
  focused = false;
  controlType = 'input-button';
  errorState = false;
  private _value: IPrograma;
  private _placeholder: string;
  private _required = false;
  private _disabled = false;
  autofilled?: boolean;
  userAriaDescribedBy?: string;

  private onChange: (value: any) => void = () => { };
  private onTouched = () => { };

  private _destroy = new Subject<void>();

  @Input()
  get placeholder(): string {
    return this._placeholder || '';
  }
  set placeholder(value: string) {
    this._placeholder = value;
    this.stateChanges.next();
  }

  @Input()
  get required(): boolean {
    return this._required;
  }
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
    this.stateChanges.next();
  }

  @Input()
  get disabled(): boolean {
    return this._disabled;
  }
  set disabled(value: boolean) {
    this._disabled = coerceBooleanProperty(value);
    this.inputControl.disabled ? this.inputControl.disable() : this.inputControl.enable();
    this.stateChanges.next();
  }

  inputControl = new FormControl('');

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    private dialog: MatDialog,
    @Self() @Optional() public ngControl: NgControl,
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


  ngOnInit(): void {
  }

  showDialog(): void {
    const dialogRef = this.dialog.open(SearchPlanInvestigacionModalComponent, {
      width: '70vw',
      data: {
        readonly: false
      } as SearchPlanInvestigacionModalData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.propagateChanges(result.programa ?? result.plan);
      }
    });
  }

  get value(): IPrograma {
    return this._value;
  }

  set value(value: IPrograma) {
    if (value !== this._value) {
      this._value = value;
      this.propagateChanges(value);
    }
  }

  writeValue(value: IPrograma): void {
    if (value !== this._value) {
      this._value = value;
      this.inputControl.setValue(value?.nombre ?? '', { emitEvent: false });
    }
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    this.inputControl.disabled ? this.inputControl.disable() : this.inputControl.enable();
    this.stateChanges.next();
  }

  onContainerClick(): void {
    this.focused = true;
    this.stateChanges.next();
  }

  get shouldLabelFloat(): boolean {
    return this.focused || !!this.value || !!this.inputControl.value;
  }

  ngOnDestroy(): void {
    this.stateChanges.complete();
    this._destroy.next();
    this._destroy.complete();
  }

  private propagateChanges(value: IPrograma): void {
    this._value = value;
    this.inputControl.setValue(value?.nombre ?? '', { emitEvent: false });
    this.onChange(value);
    this.stateChanges.next();
    this.changeDetectorRef.markForCheck();
  }

  setDescribedByIds(ids: string[]): void {
    this.userAriaDescribedBy = ids.join(' ');
    this.elementRef.nativeElement.setAttribute('aria-describedby', this.userAriaDescribedBy);
  }

  get empty(): boolean {
    return !this.value && !this.inputControl.value;
  }

  protected handleKeydown(event: KeyboardEvent): void {
    if (!this.disabled && this.focused) {
      const keyCode = event.keyCode;
      if ((keyCode === BACKSPACE || keyCode === DELETE) && !hasModifierKey(event)) {
        event.preventDefault();
        this.propagateChanges(null);
      } else {
        event.preventDefault();
        this.showDialog();
      }
    }
  }

}