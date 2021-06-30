import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si tiene relleno el id
 */
export class NullIdValidador {
  isValid(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (typeof control.value === 'string' && control.value.toString().length === 0) {
        return { required: true };
      }
      if (control.value && control.value.id) {
        return null;
      }
      return { vacio: true } as ValidationErrors;
    };
  }
}
