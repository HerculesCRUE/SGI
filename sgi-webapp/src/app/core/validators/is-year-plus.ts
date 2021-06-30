import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DateTime } from 'luxon';

export class IsYearPlus {
  static isValid(num: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value) {
        const year = Number(control.value);
        if (!isNaN(year)) {
          const diff = year - DateTime.now().year;
          if (diff <= num) {
            return null;
          }
        }
      }
      return { isPlusYear: true } as ValidationErrors;
    };
  }
}
