import { AbstractControl, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar una hora
 */
export class MinutoValidador {
  isValid(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      const value = control.value;
      if (value === undefined || value === null) {
        return { vacio: true };
      }
      const numValue = Number(value);
      if (isNaN(numValue)) {
        return { NaN: true };
      }
      if (numValue >= 0 && numValue <= 59) {
        return null;
      }
      return { noValido: true };
    };
  }
}
