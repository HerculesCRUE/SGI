import { ValidatorFn, FormGroup, ValidationErrors, AbstractControl } from '@angular/forms';

export class SelectValidator {

  /**
   * Comprueba que el valor esté incluido en la lista.
   *
   * @param required si el campo es requerido se da error cuando el campo es vacío
   * @param options Lista con los valores del selector.
   */
  static isSelectOption(options: string[], required?: boolean): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (typeof control.value === 'string') {
        if (required ? (control.value === '') : false) {
          return { invalidValue: true };
        }
        if (control.value && options && !options.includes(control.value)) {
          return { invalidValue: true };
        }
      }
      return null;
    };
  }

}
