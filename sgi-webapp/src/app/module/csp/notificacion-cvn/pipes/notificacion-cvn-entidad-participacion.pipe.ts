import { Pipe, PipeTransform } from '@angular/core';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';

@Pipe({
  name: 'notificacionCvnEntidadParticipacion'
})
export class NotificacionCvnEntidadParticipacionPipe implements PipeTransform {

  transform(notificacion: INotificacionProyectoExternoCVN): string {
    if (notificacion?.entidadParticipacion?.id) {
      return notificacion.entidadParticipacion.nombre;
    }
    return notificacion.datosEntidadParticipacion ?? '';
  }
}
