import { Pipe, PipeTransform } from '@angular/core';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';

@Pipe({
  name: 'notificacionCvnResponsable'
})
export class NotificacionCvnResponsablePipe implements PipeTransform {

  transform(notificacion: INotificacionProyectoExternoCVN): string {
    if (notificacion?.responsable?.id && notificacion?.responsable?.nombre) {
      return `${notificacion.responsable.nombre} ${notificacion.responsable.apellidos}`;
    }
    return notificacion.datosResponsable ?? '';
  }
}
