import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate,
  RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
// tslint:disable-next-line: max-line-length
import { PERIODO_TITULARIDAD_TITULAR_REQUEST_CONVERTER } from '../periodo-titularidad-titular/periodo-titularidad-titular-request.converter';
import { IPeriodoTitularidadTitularResponse } from '../periodo-titularidad-titular/periodo-titularidad-titular-response';
import { PERIODO_TITULARIDAD_TITULAR_RESPONSE_CONVERTER } from '../periodo-titularidad-titular/periodo-titularidad-titular-response.converter';
import { IPeriodoTitularidadRequest } from './periodo-titularidad-request';
import { PERIODO_TITULARIDAD_REQUEST_CONVERTER } from './periodo-titularidad-request.converter';
import { IPeriodoTitularidadResponse } from './periodo-titularidad-response';
import { PERIODO_TITULARIDAD_RESPONSE_CONVERTER } from './periodo-titularidad-response.converter';

// tslint:disable-next-line: variable-name
const _PeriodoTitularidadServiceMixinBase:
  CreateCtor<IPeriodoTitularidad, IPeriodoTitularidad, IPeriodoTitularidadRequest, IPeriodoTitularidadResponse> &
  UpdateCtor<number, IPeriodoTitularidad, IPeriodoTitularidad, IPeriodoTitularidadRequest, IPeriodoTitularidadResponse> &
  FindByIdCtor<number, IPeriodoTitularidad, IPeriodoTitularidadResponse> &
  FindAllCtor<IPeriodoTitularidad, IPeriodoTitularidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PERIODO_TITULARIDAD_REQUEST_CONVERTER,
          PERIODO_TITULARIDAD_RESPONSE_CONVERTER
        ),
        PERIODO_TITULARIDAD_REQUEST_CONVERTER,
        PERIODO_TITULARIDAD_RESPONSE_CONVERTER
      ),
      PERIODO_TITULARIDAD_RESPONSE_CONVERTER
    ),
    PERIODO_TITULARIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class PeriodoTitularidadService extends _PeriodoTitularidadServiceMixinBase {
  private static readonly MAPPING = '/periodostitularidad';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${PeriodoTitularidadService.MAPPING}`,
      http,
    );
  }

  /**
   * Devuelve todos los {@link IPeriodoTitularidadTitular} asociados al {@link IPeriodoTitularidad} pasado por parametros
   * @param periodoTitularidadId Id del {@link IPeriodoTitularidad}
   * @returns Listado de {@link IPeriodoTitularidadTitular}
   */
  findTitularesByPeriodoTitularidad(periodoTitularidadId: number): Observable<SgiRestListResult<IPeriodoTitularidadTitular>> {

    return this.find<IPeriodoTitularidadTitularResponse, IPeriodoTitularidadTitular>(
      `${this.endpointUrl}/${periodoTitularidadId}/titulares`,
      null,
      PERIODO_TITULARIDAD_TITULAR_RESPONSE_CONVERTER
    );
  }

  /**
   * Persiste los cambios efectuados a las entidades {@link IPeriodoTitularidadTitular}.
   *
   * @param idPeriodoTitularidad Id del {@link IPeriodoTitularidad}
   * @param titulares Listado de {@link IPeriodoTitularidadTitular} modificados
   */
  bulkSaveOrUpdatePeriodoTitularidadTitulares(idPeriodoTitularidad: number, titulares: IPeriodoTitularidadTitular[]) {
    return this.http.patch<IPeriodoTitularidadTitularResponse[]>(`${this.endpointUrl}/${idPeriodoTitularidad}/titulares`,
      PERIODO_TITULARIDAD_TITULAR_REQUEST_CONVERTER.fromTargetArray(titulares)
    ).pipe(
      map((response => PERIODO_TITULARIDAD_TITULAR_RESPONSE_CONVERTER.toTargetArray(response ?? [])))
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
