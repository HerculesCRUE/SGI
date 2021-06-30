import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class ListValidador {
  static contains(list: any[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      for (const value of list) {
        if (value === control.value) {
          return { contains: true } as ValidationErrors;
        }
      }
      return null;
    };
  }
}
