import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { environment } from '@env';
import { SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class TipoEvaluacionService extends SgiRestService<number, TipoEvaluacion> {
  private static readonly MAPPING = '/tipoevaluaciones';

  constructor(protected http: HttpClient) {
    super(
      TipoEvaluacionService.name,
      `${environment.serviceServers.eti}${TipoEvaluacionService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve el los tipos de evaluación: Memoria y Retrospectiva.
   */

  findTipoEvaluacionMemoriaRetrospectiva(): Observable<SgiRestListResult<TipoEvaluacion>> {
    return this.find<TipoEvaluacion, TipoEvaluacion>(`${this.endpointUrl}/memoria-retrospectiva`, null);
  }

  /**
   * Devuelve los tipos de evaluación: Seguimiento Anual y Seguimiento final.
   */

  findTipoEvaluacionSeguimientoAnualFinal(): Observable<SgiRestListResult<TipoEvaluacion>> {
    return this.find<TipoEvaluacion, TipoEvaluacion>(`${this.endpointUrl}/seguimiento-anual-final`, null);
  }
}
