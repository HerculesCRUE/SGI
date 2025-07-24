import { Pipe, PipeTransform } from '@angular/core';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { LanguageService } from '@core/services/language.service';

@Pipe({
  name: 'requerimientoJustificacionNombre',
  // El recalculo del numRequerimiento muta la entidad y no dispara el pipe en SeguimientoJustificacionRequerimientoDatosGeneralesComponent
  pure: false
})
export class RequerimientoJustificacionNombrePipe implements PipeTransform {

  constructor(private languageService: LanguageService) {
  }

  transform(requerimientoJustificacion: IRequerimientoJustificacion): string {
    return formatRequerimientoJustificacionNombre(requerimientoJustificacion, this.languageService);
  }
}

export function formatRequerimientoJustificacionNombre(requerimientoJustificacion: IRequerimientoJustificacion, languageService: LanguageService): string {
  if (requerimientoJustificacion?.numRequerimiento && requerimientoJustificacion?.tipoRequerimiento?.nombre?.length) {
    return `${requerimientoJustificacion.numRequerimiento} - ${languageService.getFieldValue(requerimientoJustificacion.tipoRequerimiento.nombre)}`;
  } else {
    return '';
  }
}
