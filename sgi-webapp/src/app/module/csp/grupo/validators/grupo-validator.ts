import { AbstractControl, AsyncValidatorFn, ValidationErrors, ValidatorFn } from '@angular/forms';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoEquipoListado } from '../grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.fragment';

export class GrupoValidator {

  /**
   * Comprueba si el codigo esta duplicado
   * 
   * @param grupoService GrupoService.
   * @param grupoId flag que indica si ya existe un tramo de dicho tipo.
   * @returns el error o null en caso de no existir error.
   */
  static duplicatedCodigo(grupoService: GrupoService, grupoId: number): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors> => {
      return grupoService.isDuplicatedCodigo(control.value, grupoId).pipe(
        map(result => result ? { duplicated: true } : null)
      );
    };
  }

  /**
   * Comprueba si existe una fecha de inicio de participacion de alguno de los miembros
   * ya añadidos al equipo que sea anterior a la nueva fecha de inicio del grupo.
   *
   * @param equipoInvestigacion miembros del equipo.
   * @returns el error o null en caso de no existir error.
   */
  static fechaInicioConflictsEquipoInvestigacion(equipoInvestigacion: IGrupoEquipoListado[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors => {
      const fechaInicioGrupo = control.value as DateTime;
      if (!!!fechaInicioGrupo) {
        return null;
      }
      return equipoInvestigacion
        .some(miembro => miembro.fechaInicio.toMillis() - fechaInicioGrupo.toMillis() < 0) ?
        { fechaInicioConflictsEquipoInvestigacion: true } : null;
    };
  }

  /**
   * Comprueba si existe una fecha de fin de participacion de alguno de los miembros
   * ya añadidos al equipo que sea posterior a la nueva fecha de fin del grupo.
   *
   * @param equipoInvestigacion miembros del equipo.
   * @returns el error o null en caso de no existir error.
   */
  static fechaFinConflictsEquipoInvestigacion(equipoInvestigacion: IGrupoEquipoListado[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors => {
      const fechaFinGrupo = control.value as DateTime;
      if (!!!fechaFinGrupo) {
        return null;
      }
      return equipoInvestigacion
        .filter(miembro => !!miembro.fechaFin)
        .some(miembro => miembro.fechaFin.toMillis() - fechaFinGrupo.toMillis() > 0) ?
        { fechaFinConflictsEquipoInvestigacion: true } : null;
    };
  }
}
