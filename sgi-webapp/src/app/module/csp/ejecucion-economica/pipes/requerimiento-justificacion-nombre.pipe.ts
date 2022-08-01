import { Pipe, PipeTransform } from '@angular/core';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';

@Pipe({
  name: 'requerimientoJustificacionNombre'
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
