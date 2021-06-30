import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es una entidad
 */
export class IsEntityValidator {

  /**
   * Comrpueba que si el campo esta relleno sea con un objeto que tenga algun valor en la propiedad id.
   */
  static isValid(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || typeof control.value === 'string'
        && control.value.toString().length === 0) {
        return null;
      }
      if (control.value && control.value.id) {
        return null;
      }
      return { invalid: true } as ValidationErrors;
    };
  }
}


