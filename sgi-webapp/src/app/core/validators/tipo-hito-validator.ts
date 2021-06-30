import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { DateTime } from 'luxon';

/**
 * Comprueba que la fecha no se superpone con ninguna otra de la lista
 *
 * @param dateFieldName Nombre del campo de la fecha.
 * @param fechas Lista de fechas con las que se quiere comprobar.
 */
export class TipoHitoValidator {
  static notInDate(dateFieldName: string, fechas: DateTime[], tipoHito: ITipoHito[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaControl = formGroup.controls[dateFieldName];

      if ((fechaControl.errors && !fechaControl.errors.notIn)) {
        return;
      }

      if (fechas.some(fecha => fecha.equals(fechaControl.value) &&
        tipoHito.some(hito => formGroup.controls.tipoHito.value.id === hito.id))) {
        fechaControl.setErrors({ notIn: true });
        fechaControl.markAsTouched({ onlySelf: true });
      } else {
        if (fechaControl.errors) {
          delete fechaControl.errors.notIn;
          fechaControl.updateValueAndValidity({ onlySelf: true });
        }
      }
    };
  }
}
