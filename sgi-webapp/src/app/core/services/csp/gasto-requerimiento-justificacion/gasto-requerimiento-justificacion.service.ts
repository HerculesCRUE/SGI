import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IGastoRequerimientoJustificacionRequest } from './gasto-requerimiento-justificacion-request';
import { GASTO_REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER } from './gasto-requerimiento-justificacion-request.converter';
import { IGastoRequerimientoJustificacionResponse } from './gasto-requerimiento-justificacion-response';
import { GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from './gasto-requerimiento-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _GastoRequerimientoJustificacionMixinBase:
  FindAllCtor<IGastoRequerimientoJustificacion, IGastoRequerimientoJustificacionResponse> &
  CreateCtor<IGastoRequerimientoJustificacion, IGastoRequerimientoJustificacion,
    IGastoRequerimientoJustificacionRequest, IGastoRequerimientoJustificacionResponse> &
  UpdateCtor<number, IGastoRequerimientoJustificacion, IGastoRequerimientoJustificacion,
    IGastoRequerimientoJustificacionRequest, IGastoRequerimientoJustificacionResponse> &
  typeof SgiRestBaseService =
  mixinFindAll(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        GASTO_REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
        GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
      ),
      GASTO_REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
      GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER),
    GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class GastoRequerimientoJustificacionService extends _GastoRequerimientoJustificacionMixinBase {

  private static readonly MAPPING = '/gastos-requerimientos-justificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GastoRequerimientoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Elimina un Gasto Requerimiento Justificacion por id.
   *
   * @param id Id del Gasto Requerimiento Justificacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

  existsWithIndentificadorJustificacion(identificadorJustificacion: string): Observable<boolean> {
    const url = `${this.endpointUrl}/byidentificadorjustificacion`;
    return this.http.head(url, { params: { identificadorJustificacion }, observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

}
