import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_ALEGACION_REQUEST_CONVERTER } from './incidencia-documentacion-requerimiento-alegacion-request.converter';
import { IIncidenciaDocumentacionRequerimientoRequest } from './incidencia-documentacion-requerimiento-request';
import { INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_INCIDENCIA_REQUEST_CONVERTER } from './incidencia-documentacion-requerimiento-request.converter';
import { IIncidenciaDocumentacionRequerimientoResponse } from './incidencia-documentacion-requerimiento-response';
import { INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER } from './incidencia-documentacion-requerimiento-response.converter';

// tslint:disable-next-line: variable-name
const _IncidenciaDocumentacionRequerimientoMixinBase:
  CreateCtor<IIncidenciaDocumentacionRequerimiento, IIncidenciaDocumentacionRequerimiento,
    IIncidenciaDocumentacionRequerimientoRequest, IIncidenciaDocumentacionRequerimientoResponse> &
  UpdateCtor<number, IIncidenciaDocumentacionRequerimiento, IIncidenciaDocumentacionRequerimiento,
    IIncidenciaDocumentacionRequerimientoRequest, IIncidenciaDocumentacionRequerimientoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_INCIDENCIA_REQUEST_CONVERTER,
      INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER
    ),
    INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_INCIDENCIA_REQUEST_CONVERTER,
    INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class IncidenciaDocumentacionRequerimientoService extends _IncidenciaDocumentacionRequerimientoMixinBase {

  private static readonly MAPPING = '/incidencias-documentacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${IncidenciaDocumentacionRequerimientoService.MAPPING}`,
      http
    );
  }

  /**
   * Elimina una Incidencia Documentacion Requerimiento por id.
   *
   * @param id Id de la Incidencia Documentacion Requerimiento
   */
  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

  updateAlegacion(id: number, incidenciaDocumentacion: IIncidenciaDocumentacionRequerimiento):
    Observable<IIncidenciaDocumentacionRequerimiento> {
    return this.http.patch<IIncidenciaDocumentacionRequerimientoResponse>(
      `${this.endpointUrl}/${id}/alegar`,
      INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_ALEGACION_REQUEST_CONVERTER.fromTarget(incidenciaDocumentacion))
      .pipe(
        map(response => INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER.toTarget(response))
      );
  }
}
