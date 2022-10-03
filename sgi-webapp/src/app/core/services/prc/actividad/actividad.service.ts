import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IActividad } from '@core/models/prc/actividad';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IActividadResponse } from './actividad-response';
import { ACTIVIDAD_RESPONSE_CONVERTER } from './actividad-response.converter';

// tslint:disable-next-line: variable-name
const _ActividadMixinBase:
  FindAllCtor<IActividad, IActividadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    ACTIVIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ActividadService extends _ActividadMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/actividades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ActividadService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra las actividades a los que pertenece el investigador actual
   *
   * @param options opciones de búsqueda.
   */
  findActividadesInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IActividad>> {
    return this.find<IActividadResponse, IActividad>(
      `${this.endpointUrl}/investigador`,
      options,
      ACTIVIDAD_RESPONSE_CONVERTER
    );
  }
}
