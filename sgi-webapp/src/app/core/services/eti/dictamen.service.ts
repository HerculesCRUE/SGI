import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDictamen } from '@core/models/eti/dictamen';
import { environment } from '@env';
import { SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DictamenService extends SgiRestService<number, IDictamen> {
  private static readonly MAPPING = '/dictamenes';

  constructor(protected http: HttpClient) {
    super(
      DictamenService.name,
      `${environment.serviceServers.eti}${DictamenService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria
   * y el TipoEstadoMemoria sea En secretaría revisión mínima
   *
   */
  findAllByMemoriaRevisionMinima(): Observable<SgiRestListResult<IDictamen>> {
    return this.find<IDictamen, IDictamen>(`${this.endpointUrl}/memoria-revision-minima`, null);
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria
   * y el TipoEstadoMemoria NO esté En secretaría revisión mínima
   *
   */
  findAllByMemoriaNoRevisionMinima(): Observable<SgiRestListResult<IDictamen>> {
    return this.find<IDictamen, IDictamen>(`${this.endpointUrl}/memoria-no-revision-minima`, null);
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Retrospectiva
   */

  findAllByRetrospectiva(): Observable<SgiRestListResult<IDictamen>> {
    return this.find<IDictamen, IDictamen>(`${this.endpointUrl}/retrospectiva`, null);
  }
}
