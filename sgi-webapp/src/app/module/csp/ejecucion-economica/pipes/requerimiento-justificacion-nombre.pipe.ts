import { Pipe, PipeTransform } from '@angular/core';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';

@Pipe({
  name: 'requerimientoJustificacionNombre',
  // El recalculo del numRequerimiento muta la entidad y no dispara el pipe en SeguimientoJustificacionRequerimientoDatosGeneralesComponent
  pure: false
})
export class RequerimientoJustificacionNombrePipe implements PipeTransform {

  transform(requerimientoJustificacion: IRequerimientoJustificacion): string {
    return formatRequerimientoJustificacionNombre(requerimientoJustificacion);
  }
}

export function formatRequerimientoJustificacionNombre(requerimientoJustificacion: IRequerimientoJustificacion): string {
  if (requerimientoJustificacion?.numRequerimiento && requerimientoJustificacion?.tipoRequerimiento?.nombre) {
    return `${requerimientoJustificacion.numRequerimiento} - ${requerimientoJustificacion.tipoRequerimiento.nombre}`;
  } else {
    return '';
  }
}
