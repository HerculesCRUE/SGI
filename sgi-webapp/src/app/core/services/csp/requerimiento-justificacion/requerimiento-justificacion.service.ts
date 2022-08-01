import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { IRequerimientoJustificacionResponse } from './requerimiento-justificacion-response';
import { REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from './requerimiento-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _RequerimientoJustificacionMixinBase:
  FindByIdCtor<number, IRequerimientoJustificacion, IRequerimientoJustificacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class RequerimientoJustificacionService extends _RequerimientoJustificacionMixinBase {

  private static readonly MAPPING = '/requerimientosjustificacion';

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
}
