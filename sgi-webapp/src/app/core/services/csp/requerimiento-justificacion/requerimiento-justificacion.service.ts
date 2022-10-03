import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById,
  mixinUpdate, SgiRestBaseService, SgiRestFindOptions,
  SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IAlegacionRequerimientoResponse } from '../alegacion-requerimiento/alegacion-requerimiento-response';
import { ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER } from '../alegacion-requerimiento/alegacion-requerimiento-response.converter';
import { IGastoRequerimientoJustificacionResponse } from '../gasto-requerimiento-justificacion/gasto-requerimiento-justificacion-response';
import { GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from '../gasto-requerimiento-justificacion/gasto-requerimiento-justificacion-response.converter';
import { IIncidenciaDocumentacionRequerimientoResponse } from '../incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento-response';
import { INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER } from '../incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento-response.converter';
import { IRequerimientoJustificacionRequest } from './requerimiento-justificacion-request';
import { REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER } from './requerimiento-justificacion-request.converter';
import { IRequerimientoJustificacionResponse } from './requerimiento-justificacion-response';
import { REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from './requerimiento-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _RequerimientoJustificacionMixinBase:
  FindByIdCtor<number, IRequerimientoJustificacion, IRequerimientoJustificacionResponse> &
  CreateCtor<IRequerimientoJustificacion, IRequerimientoJustificacion,
    IRequerimientoJustificacionRequest, IRequerimientoJustificacionResponse> &
  UpdateCtor<number, IRequerimientoJustificacion, IRequerimientoJustificacion,
    IRequerimientoJustificacionRequest, IRequerimientoJustificacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
        REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
      ),
      REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
      REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
    ),
    REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class RequerimientoJustificacionService extends _RequerimientoJustificacionMixinBase {

  private static readonly MAPPING = '/requerimientos-justificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RequerimientoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Elimina un Requerimiento Justificacion por id.
   *
   * @param id Id del Requerimiento Justificacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

  findIncidenciasDocumentacion(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IIncidenciaDocumentacionRequerimiento>> {
    return this.find<IIncidenciaDocumentacionRequerimientoResponse, IIncidenciaDocumentacionRequerimiento>(
      `${this.endpointUrl}/${id}/incidencias-documentacion`,
      options,
      INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER);
  }

  findGastos(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IGastoRequerimientoJustificacion>> {
    return this.find<IGastoRequerimientoJustificacionResponse, IGastoRequerimientoJustificacion>(
      `${this.endpointUrl}/${id}/gastos`,
      options,
      GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER);
  }

  findAlegacion(id: number):
    Observable<IAlegacionRequerimiento> {
    return this.http.get<IAlegacionRequerimientoResponse>(`${this.endpointUrl}/${id}/alegacion`)
      .pipe(
        map(response => ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER.toTarget(response))
      );
  }
}
