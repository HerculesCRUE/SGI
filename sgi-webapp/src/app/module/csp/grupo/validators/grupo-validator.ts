import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

}