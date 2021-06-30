import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/convocatoria-periodo-justificacion.converter';
import { IConvocatoriaPeriodoJustificacionBackend } from '@core/models/csp/backend/convocatoria-periodo-justificacion-backend';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPeriodoJustificacionService
  extends SgiMutableRestService<number, IConvocatoriaPeriodoJustificacionBackend, IConvocatoriaPeriodoJustificacion> {
  private static readonly MAPPING = '/convocatoriaperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaPeriodoJustificacionService.name,
      `${environment.serviceServers.csp}${ConvocatoriaPeriodoJustificacionService.MAPPING}`,
      http,
      CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER
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
    return this.http.patch<IConvocatoriaPeriodoJustificacionBackend[]>(
      `${this.endpointUrl}/${convocatoriaId}`,
      this.converter.fromTargetArray(periodosJustificacion)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

}
