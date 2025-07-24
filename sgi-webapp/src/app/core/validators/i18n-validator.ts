import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export class I18nValidators {

  static required(control: AbstractControl): ValidationErrors | null {
    if (Array.isArray(control.value) && control.value.length > 0) {
      const data = control.value as I18nFieldValue[];
      if (data.every(v => v.value != null && v.value.length > 0)) {
        return null;
      }
    }

    return { 'required': true };
  };

  public static maxLength(maxLength: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (Array.isArray(control.value) && control.value.length > 0) {
        const data = control.value as I18nFieldValue[];
        if (data.some(v => v.value != null && v.value.length > maxLength)) {
          return { 'maxlength': { 'requiredLength': maxLength, 'actualLength': control.value.length } };
        }

        return null;
      };
    }
  }
}
