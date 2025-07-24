import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoJustificacionResponse } from '@core/services/csp/convocatoria-periodo-justificacion/convocatoria-periodo-justificacion-response';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CONVOCATORIA_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from './convocatoria-periodo-justificacion-response.converter';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPeriodoJustificacionService extends SgiRestBaseService {
  private static readonly MAPPING = '/convocatoriaperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza la lista IConvocatoriaPeriodoJustificacion de la convocatoria con los periodos de periodosJustificacion
   * creando los nuevos, actualizando los existentes y eliminando los existentes que no esten en la lista.
   *
   * @param convocatoriaId Id de la convocatoria
   * @param periodosJustificacion Lista de IConvocatoriaPeriodoJustificacion
   * @returns Lista de IConvocatoriaPeriodoJustificacion actualizada
   */
  updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId: number, periodosJustificacion: IConvocatoriaPeriodoJustificacion[]):
    Observable<IConvocatoriaPeriodoJustificacion[]> {
    return this.http.patch<IConvocatoriaPeriodoJustificacionResponse[]>(
      `${this.endpointUrl}/${convocatoriaId}`,
      CONVOCATORIA_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.fromTargetArray(periodosJustificacion)
    ).pipe(
      map(response => CONVOCATORIA_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
