import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER } from '@core/converters/csp/convocatoria-periodo-seguimiento-cientifico.converter';
import { IConvocatoriaPeriodoSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-periodo-seguimiento-cientifico-backend';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaSeguimientoCientificoService
  extends SgiMutableRestService<number, IConvocatoriaPeriodoSeguimientoCientificoBackend, IConvocatoriaPeriodoSeguimientoCientifico> {
  private static readonly MAPPING = '/convocatoriaperiodoseguimientocientificos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaSeguimientoCientificoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaSeguimientoCientificoService.MAPPING}`,
      http,
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER
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
    return this.http.patch<IConvocatoriaPeriodoSeguimientoCientificoBackend[]>(
      `${this.endpointUrl}/${convocatoriaId}`,
      this.converter.fromTargetArray(periodosJustificacion)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

}
