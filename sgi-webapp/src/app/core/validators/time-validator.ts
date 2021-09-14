import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class TimeValidator {

  static isAfterOther(other: AbstractControl): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value && other.value) {
        if (control.value.getTime() < other.value.getTime()) {
          return { after: true };
        }
      }
      return null;
    };
  }

  static isBeforeOther(other: AbstractControl): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value && other.value) {
        if (control.value.getTime() > other.value.getTime()) {
          return { before: true };
        }
      }
      return null;
    };
  }

}
