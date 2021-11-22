import { AbstractControl, ValidatorFn } from '@angular/forms';
import { Tipo } from '@core/models/pii/tramo-reparto';

export class TramoRepartoValidator {

  /**
   * Comprueba que el Tipo de tramo recibido por parámetros no se pueda estar duplicado
   * @param tipo tipo de tramo a comprobar.
   * @param hasTipoTramo flag que indica si ya existe un tramo de dicho tipo.
   * @returns el error o null en caso de no existir error.
   */
  static noDuplicateTipoTramo(tipo: Tipo, hasTipoTramo: boolean): ValidatorFn {
    return (control: AbstractControl): { [key: string]: Tipo } | null => {
      return hasTipoTramo && control.value === tipo ? { noDuplicateTipoTramo: tipo } : null;
    };
  }
}