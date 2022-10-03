import { AbstractControl, ValidatorFn } from '@angular/forms';
import { TipoTemporalidad } from '@core/models/prc/rango';

export class RangoValidator {

  /**
   * Comprueba que el Tipo de temporalidad recibido por parámetros no esta duplicado
   * @param tipo tipo de temporalidad a comprobar.
   * @param hasTipoTemporalidad flag que indica si ya existe un rango de dicho tipo.
   * @returns el error o null en caso de no existir error.
   */
  static noDuplicateTipoTemporalidad(tipo: TipoTemporalidad, hasTipoTemporalidad: boolean): ValidatorFn {
    return (control: AbstractControl): { [key: string]: TipoTemporalidad } | null => {
      return hasTipoTemporalidad && control.value === tipo ? { duplicated: tipo } : null;
    };
  }

  /**
 * Comprueba que el Tipo de temporalidad de rango inicial existe cuando se quiere crear un rango de tipo "INTERMEDIO"
 * @param tipo tipo de temporalidad a comprobar.
 * @param hasRangoInicial flag que indica si ya existe un rango de tipo inicial.
 * @returns el error o null en caso de no existir error.
 */
  static hasRangoInicial(tipo: TipoTemporalidad, hasRangoInicial: boolean): ValidatorFn {
    return (control: AbstractControl): { [key: string]: TipoTemporalidad } | null => {
      return !hasRangoInicial && control.value === tipo ? { notHasRangoInicial: tipo } : null;
    };
  }
}