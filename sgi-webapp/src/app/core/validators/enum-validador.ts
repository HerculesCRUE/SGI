import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class EnumValidador {
  static isValid(enumType: any): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || typeof control.value === 'string' && control.value.toString().length === 0) {
        return null;
      }
      if (control.value && Object.values(enumType).some((value) => value === control.value)) {
        return null;
      }
      return { invalid: true } as ValidationErrors;
    };
  }
}
