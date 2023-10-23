import { NumberSymbol, formatNumber, getLocaleNumberSymbol } from '@angular/common';
import { Directive, ElementRef, HostListener, Inject, Input, LOCALE_ID } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MAT_INPUT_VALUE_ACCESSOR } from '@angular/material/input';

@Directive({
  selector: 'input[sgiInputWithThousandSeparator]',
  providers: [
    { provide: MAT_INPUT_VALUE_ACCESSOR, useExisting: SgiInputWithThousandSeparator }
  ]
})
export class SgiInputWithThousandSeparator {
  // tslint:disable-next-line:variable-name
  private _value: number | null;
  private stringValue: string;

  constructor(
    private elementRef: ElementRef<HTMLInputElement>,
    public ngControl: NgControl,
    @Inject(LOCALE_ID) private locale: string
  ) {
    ngControl.valueAccessor = this; // Remove NG_VALUE_ACCESSOR from providers to remove cyclic dependency with NgControl
  }

  get value(): string | number | null {
    return this._value;
  }

  @Input('value')
  set value(value: string | number | null) {
    this._value = +value;
    this.formatValue(+value);
  }

  @HostListener('input', ['$event.target.value'])
  onInput(value) {
    this._value = value;
  }

  @HostListener('blur')
  _onBlur() {
    this.formatValue(this._value);
  }

  @HostListener('focus')
  onFocus() {
    this.unFormatValue();
  }

  @HostListener('input', ['$event'])
  onInputChange(event) {
    const decimalSeparatorLocale = getLocaleNumberSymbol(this.locale, NumberSymbol.Decimal);
    const initalValue = this.elementRef.nativeElement.value.replace(decimalSeparatorLocale, '.');
    let value = initalValue.replace(/[^0-9.]*/g, '');
    if ((value?.match(/\./g)?.length ?? 0) > 1 || (value.indexOf('.') >= 0 && value.indexOf('.') < value.length - 3)) {
      value = value.substring(0, value.length - 1);
    }

    if (initalValue !== value) {
      event.stopPropagation();
    }

    this.elementRef.nativeElement.value = value;
    this._value = (!value || isNaN(+value)) ? (value === '0' ? 0 : null) : +value;
    this._onChange(this._value);
  }


  writeValue(value: any) {
    this._value = value;
    this.formatValue(this._value);
  }

  registerOnChange(fn: (value: any) => void) {
    this._onChange = fn;
  }

  registerOnTouched = () => { };

  private _onChange: (value: any) => void = () => { };

  private formatValue(value: number | null): void {
    if (value !== null) {
      this.stringValue = formatNumber(+value, this.locale, '1.2-2');
    } else {
      this.stringValue = '';
    }

    this.elementRef.nativeElement.value = this.stringValue;
    if (this.ngControl) {
      this.ngControl.control?.markAsTouched(); // Touch input to allow MatFormField to show errors properly
    }
  }

  private unFormatValue(): void {
    this.elementRef.nativeElement.value = this._value?.toString() ?? '';
  }

}
