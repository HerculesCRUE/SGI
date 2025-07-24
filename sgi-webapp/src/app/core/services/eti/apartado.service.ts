import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IApartadoResponse } from './apartado/apartado-response';
import { APARTADO_RESPONSE_CONVERTER } from './apartado/apartado-response.converter';

// tslint:disable-next-line: variable-name
const _ApartadoServiceMixinBase:
  FindByIdCtor<number, IApartado, IApartadoResponse> &
  FindAllCtor<IApartado, IApartadoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      APARTADO_RESPONSE_CONVERTER
    ),
    APARTADO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ApartadoService extends _ApartadoServiceMixinBase {
  private static readonly MAPPING = '/apartados';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ApartadoService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los apartados hijos de un apartado
   *
   * @param id Id del apartado
   * @param options Opciones de paginación
   */
  getHijos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartadoResponse, IApartado>(`${this.endpointUrl}/${id}/hijos`, options, APARTADO_RESPONSE_CONVERTER);
  }
}
