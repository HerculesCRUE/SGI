import { Pipe, PipeTransform } from '@angular/core';
import { IPersona } from '@core/models/sgp/persona';

@Pipe({
  name: 'personaEntidadPublic'
})
export class PersonaEntidadPublicPipe implements PipeTransform {

  transform({ entidad, entidadPropia }: IPersona): string {
    let entidadTitle = '';
    if (entidad?.nombre) {
      entidadTitle = entidad.nombre;
    }
    if (entidadTitle && entidadPropia?.nombre) {
      entidadTitle += ', ' + entidadPropia.nombre;
    } else if (entidadPropia?.nombre) {
      entidadTitle = entidadPropia.nombre;
    }
    return entidadTitle;
  }
}
