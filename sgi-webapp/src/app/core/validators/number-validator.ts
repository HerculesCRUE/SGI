import { ValidatorFn, FormGroup, ValidationErrors, AbstractControl } from '@angular/forms';
import { NumberUtils } from '@core/utils/number.utils';

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
      // if a 0 number (falsy value) is patched into the form !numeroAnteriorNumber is true
      if (numeroPosteriorNumber && ((!numeroAnteriorNumber && numeroAnteriorNumber !== 0) || numeroAnteriorNumber >= numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ after: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.after;
        numeroPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  /**
   * Comprueba que el segundo numero es posterior o igual al primero.
   *
   * @param firstNumberFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondNumberFieldName Nombre del campo que se quiere validar.
   */
  static isAfterOrEqual(firstNumberFieldName: string, secondNumberFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const numeroAnteriorControl = formGroup.controls[firstNumberFieldName];
      const numeroPosteriorControl = formGroup.controls[secondNumberFieldName];

      if (numeroPosteriorControl.errors && !numeroPosteriorControl.errors.after) {
        return;
      }

      const numeroAnteriorNumber = numeroAnteriorControl.value;
      const numeroPosteriorNumber = numeroPosteriorControl.value;
      // if a 0 number (falsy value) is patched into the form !numeroAnteriorNumber is true
      if (numeroPosteriorNumber && ((!numeroAnteriorNumber && numeroAnteriorNumber !== 0)
        || numeroAnteriorNumber > numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ afterOrEqual: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.afterOrEqual;
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

  /**
   * Comprueba que la parte decimal introducida no se pase de los caracteres indicados.
   * @param max Máximo de caracteres decimales
   */
  static maxDecimalDigits(max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value && max) {
        const arrayDecimal = control.value.toString().split('.');
        if (arrayDecimal.length > 1) {
          return !isNaN(control.value) && arrayDecimal[1].length > max ? { maxDecimals: { max, actual: control.value } } : null;
        }
      }
      return null;
    };
  }

  /**
   * Comprueba que el sumatorio de los campos pasados como parámetros es igual al valor inicial
   * (Sólo hace la comprobación cuando todos los inputs indicados no tienen otros errores)
   *
   * @param value Valor al que tiene que ser igual el sumatorio de los campos indicados.
   * @param fieldsName Array con el nombre de los campos a sumar.
   */
  static fieldsSumEqualsToValue(value: number, ...fieldsName: string[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      let hasAnyControlOtherErrors = false;
      const totalValue = NumberUtils.roundNumber(fieldsName.reduce(
        (accum, fieldName) => {
          const control = formGroup.controls[fieldName];
          const { fieldsSumNotEqualsToValue, ...otherErrors } = control.errors ?? {};
          hasAnyControlOtherErrors = hasAnyControlOtherErrors || Object.keys(otherErrors).length > 0;
          const controlValue = Number(control.value === null ? NaN : control.value);
          return accum + controlValue;
        },
        0
      ));
      if (hasAnyControlOtherErrors || isNaN(totalValue) || totalValue === value) {
        fieldsName.forEach(fieldName => {
          const control = formGroup.controls[fieldName];
          if (control.errors?.fieldsSumNotEqualsToValue) {
            delete control.errors.fieldsSumNotEqualsToValue;
            control.updateValueAndValidity({ onlySelf: true });
          }
        });
        return null;
      } else {
        fieldsName.forEach(fieldName => {
          const control = formGroup.controls[fieldName];
          control.setErrors({ fieldsSumNotEqualsToValue: true });
          control.markAsTouched({ onlySelf: true });
        });
        return { fieldsSumNotEqualsToValue: { value } };
      }
    };
  }

  /**
   * Comprueba que el segundo numero es anterior o igual al primero.
   *
   * @param firstNumberFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondNumberFieldName Nombre del campo que se quiere validar.
   */
  static isBeforeOrEqual(firstNumberFieldName: string, secondNumberFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const numeroAnteriorControl = formGroup.controls[firstNumberFieldName];
      const numeroPosteriorControl = formGroup.controls[secondNumberFieldName];

      if (numeroPosteriorControl.errors && !numeroPosteriorControl.errors.after) {
        return;
      }

      const numeroAnteriorNumber = numeroAnteriorControl.value;
      const numeroPosteriorNumber = numeroPosteriorControl.value;
      // if a 0 number (falsy value) is patched into the form !numeroAnteriorNumber is true
      if (numeroPosteriorNumber && ((!numeroAnteriorNumber && numeroAnteriorNumber !== 0)
        || numeroAnteriorNumber < numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ beforeOrEqual: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.beforeOrEqual;
        numeroPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }
}
