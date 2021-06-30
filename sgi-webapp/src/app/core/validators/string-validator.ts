import { ValidatorFn, FormGroup, ValidationErrors, AbstractControl } from '@angular/forms';

export class StringValidator {

  /**
   * Comprueba que el valor no este incluido en la lista.
   *
   * @param notAllowedValues Lista con los valores que no puede tener.
   */
  static notIn(notAllowedValues: string[]): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (control.value && notAllowedValues && notAllowedValues.includes(control.value)) {
        return { notAllowed: true };
      }
      return null;
    };
  }

}
