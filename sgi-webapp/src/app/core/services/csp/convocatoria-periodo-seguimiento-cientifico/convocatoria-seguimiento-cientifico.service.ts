import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IConvocatoriaPeriodoSeguimientoCientificoResponse } from '@core/services/csp/convocatoria-periodo-seguimiento-cientifico/convocatoria-periodo-seguimiento-cientifico-response';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_RESPONSE_CONVERTER } from './convocatoria-periodo-seguimiento-cientifico-response.converter';

const _ConvocatoriaSeguimientoCientificoServiceMixinBase:
  FindByIdCtor<number, IConvocatoriaPeriodoSeguimientoCientifico, IConvocatoriaPeriodoSeguimientoCientificoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaSeguimientoCientificoService extends _ConvocatoriaSeguimientoCientificoServiceMixinBase {
  private static readonly MAPPING = '/convocatoriaperiodoseguimientocientificos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaSeguimientoCientificoService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza la lista IConvocatoriaSeguimientoCientifico de la convocatoria con los periodos de seguimiento cientifico
   * creando los nuevos, actualizando los existentes y eliminando los existentes que no esten en la lista.
   *
   * @param convocatoriaId Id de la convocatoria
   * @param periodosJustificacion Lista de IConvocatoriaSeguimientoCientifico
   * @returns Lista de IConvocatoriaSeguimientoCientifico actualizada
   */
  updateConvocatoriaSeguimientoCientificoConvocatoria(
    convocatoriaId: number,
    periodosJustificacion: IConvocatoriaPeriodoSeguimientoCientifico[]
  ): Observable<IConvocatoriaPeriodoSeguimientoCientifico[]> {
    return this.http.patch<IConvocatoriaPeriodoSeguimientoCientificoResponse[]>(
      `${this.endpointUrl}/${convocatoriaId}`,
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_RESPONSE_CONVERTER.fromTargetArray(periodosJustificacion)
    ).pipe(
      map(response => CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
