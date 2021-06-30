import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACTA_WITH_NUM_EVALUACIONES_CONVERTER } from '@core/converters/eti/acta-with-num-evaluaciones.converter';
import { ACTA_CONVERTER } from '@core/converters/eti/acta.converter';
import { IActa } from '@core/models/eti/acta';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IActaBackend } from '@core/models/eti/backend/acta-backend';
import { IActaWithNumEvaluacionesBackend } from '@core/models/eti/backend/acta-with-num-evaluaciones-backend';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ActaService extends SgiMutableRestService<number, IActaBackend, IActa> {
  private static readonly MAPPING = '/actas';

  constructor(protected http: HttpClient) {
    super(
      ActaService.name,
      `${environment.serviceServers.eti}${ActaService.MAPPING}`,
      http,
      ACTA_CONVERTER
    );
  }

  /**
   * Recupera el listado de actas activas con el número de evaluaciones iniciales, en revisión y las totales de ambas.
   * @param options opciones de búsqueda.
   * @returns listado de actas.
   */
  findActivasWithEvaluaciones(options?: SgiRestFindOptions) {
    return this.find<IActaWithNumEvaluacionesBackend, IActaWithNumEvaluaciones>(
      `${this.endpointUrl}`,
      options,
      ACTA_WITH_NUM_EVALUACIONES_CONVERTER
    );
  }

  /**
   * Finaliza el acta recibido por parámetro.
   * @param actaId id de acta.
   */
  finishActa(actaId: number): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/${actaId}/finalizar`, null);
  }

}
