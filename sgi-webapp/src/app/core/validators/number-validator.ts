import { ValidatorFn, FormGroup, ValidationErrors, AbstractControl } from '@angular/forms';

export class NumberValidator {

  /**
   * Comprueba que el segundo numero es posterior al primero.
   *
   * @param firstNumberFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondNumberFieldName Nombre del campo que se quiere validar.
   */
  static isAfter(firstNumberFieldName: string, secondNumberFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const numeroAnteriorControl = formGroup.controls[firstNumberFieldName];
      const numeroPosteriorControl = formGroup.controls[secondNumberFieldName];

      if (numeroPosteriorControl.errors && !numeroPosteriorControl.errors.after) {
        return;
      }

      const numeroAnteriorNumber = numeroAnteriorControl.value;
      const numeroPosteriorNumber = numeroPosteriorControl.value;

      if (numeroPosteriorNumber && (!numeroAnteriorNumber || numeroAnteriorNumber >= numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ after: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.after;
        numeroPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  static isAfterOptional(firstNumberFieldName: string, secondNumberFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const numeroAnteriorControl = formGroup.controls[firstNumberFieldName];
      const numeroPosteriorControl = formGroup.controls[secondNumberFieldName];

      if (!numeroAnteriorControl.value || numeroPosteriorControl.errors && !numeroPosteriorControl.errors.after) {
        this.deleteError(numeroAnteriorControl, 'after');
        this.deleteError(numeroPosteriorControl, 'after');
        return;
      }

      const numeroAnteriorNumber = numeroAnteriorControl.value;
      const numeroPosteriorNumber = numeroPosteriorControl.value;

      if (numeroPosteriorNumber && (!numeroAnteriorNumber || numeroAnteriorNumber >= numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ after: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.after;
        numeroPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  private static deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  /**
   * Comprueba que el número sea entero. Sin decimales
   */
  static isInteger(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (control.value && control.value !== parseInt(control.value, 10)) {
        if (!control.errors) {
          return { integer: true };
        }
      }
      return null;
    };
  }

  /**
   * Comprueba que la parte decimal introducida no se pase de los caracteres indicados.
   * @param max Máximo de caracteres decimales
   */
  static maxDecimalPlaces(max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value && max) {
        const arrayDecimal = control.value.toString().split('.');
        if (arrayDecimal.length > 1) {
          return !isNaN(control.value) && arrayDecimal[1].length > max ? { max: { max, actual: control.value } } : null;
        }
      }
      return null;
    };
  }

}
