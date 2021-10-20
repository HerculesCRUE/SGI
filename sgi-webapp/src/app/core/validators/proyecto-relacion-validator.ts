import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { IProyectoRelacionTableData } from 'src/app/module/csp/proyecto/proyecto-formulario/proyecto-relaciones/proyecto-relaciones.fragment';

export class ProyectoRelacionValidator {
  /**
 * Comprueba que la entidad que se intenta relacionar no esté ya relacionada.
 *
 * @param entitiesAlreadyRelated Array de entidades ya relacionadas.
 */
  static notRepeatedProyectoRelacion(entitiesAlreadyRelated: IProyectoRelacionTableData[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const entidadRelacionadaControl = formGroup.controls.entidadRelacionada;
      const tipoEntidadRelacionadaControl = formGroup.controls.tipoEntidadRelacionada;

      if (entidadRelacionadaControl.errors && !entidadRelacionadaControl.errors.notRepeatedProyectoRelacion) {
        return null;
      }

      if (entitiesAlreadyRelated.some(entity =>
        entity.tipoEntidadRelacionada === tipoEntidadRelacionadaControl.value &&
        entity.entidadRelacionada.id === entidadRelacionadaControl.value?.id)) {
        entidadRelacionadaControl.setErrors({ notRepeatedProyectoRelacion: true });
        entidadRelacionadaControl.markAsTouched({ onlySelf: true });
      } else if (entidadRelacionadaControl.errors) {
        delete entidadRelacionadaControl.errors.notRepeatedProyectoRelacion;
        entidadRelacionadaControl.updateValueAndValidity({ onlySelf: true });
      }

      return null;
    };
  }
}