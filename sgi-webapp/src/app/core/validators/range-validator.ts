import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DateTime } from 'luxon';

export interface IRange {
  inicio: number | DateTime;
  fin: number | DateTime;
}

export class RangeValidator {

  /**
   * Comprueba que el rango entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param ranges Lista de rangos con los que se quiere comprobar.
   */
  static notOverlaps(startRangeFieldName: string, endRangeFieldName: string, ranges: IRange[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];

      if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlapped)
        || (finRangoControl.errors && !finRangoControl.errors.overlapped)) {
        return;
      }

      const inicioRangoNumber = inicioRangoControl.value;
      const finRangoNumber = finRangoControl.value;

      if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
        inicioRangoControl.setErrors({ overlapped: true });
        inicioRangoControl.markAsTouched({ onlySelf: true });

        finRangoControl.setErrors({ overlapped: true });
        finRangoControl.markAsTouched({ onlySelf: true });
      } else {
        if (inicioRangoControl.errors) {
          delete inicioRangoControl.errors.overlapped;
          inicioRangoControl.updateValueAndValidity({ onlySelf: true });
        }

        if (finRangoControl.errors) {
          delete finRangoControl.errors.overlapped;
          finRangoControl.updateValueAndValidity({ onlySelf: true });
        }
      }
    };
  }

  /**
   * Comprueba que un número esta contenido en una lista
   *
   * @param values Lista de números para realizar la comprobación
   */
  static contains(values: number[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || isNaN(control.value)
        || !values || values.length === 0) {
        return null;
      }
      for (const value of values) {
        if (control.value >= value && control.value <= value) {
          return { contains: true } as ValidationErrors;
        }
      }
      return null;
    };
  }
}
