import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-periodo-seguimiento-documento.converter';
import { PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER } from '@core/converters/csp/proyecto-periodo-seguimiento.converter';
import { IProyectoPeriodoSeguimientoBackend } from '@core/models/csp/backend/proyecto-periodo-seguimiento-backend';
import { IProyectoPeriodoSeguimientoDocumentoBackend } from '@core/models/csp/backend/proyecto-periodo-seguimiento-documento-backend';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PROYECTO_PERIODO_SEGUIMIENTO_PRESENTACION_DOCUMENTACION_REQUEST_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-presentacion-documentacion-request.converter';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoService
  extends SgiMutableRestService<number, IProyectoPeriodoSeguimientoBackend, IProyectoPeriodoSeguimiento>  {
  private static readonly MAPPING = '/proyectoperiodoseguimientos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPeriodoSeguimientoService.name,
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoService.MAPPING}`,
      http,
      PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER
    );
  }

  /**
   * Comprueba si existe un proyecto periodo seguimiento
   *
   * @param id Id del proyecto periodo seguimiento
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Recupera los documentos del proyecto periodo de seguimiento
   * @param id Id del proyecto periodo de seguimiento
   * @return la lista de ProyectoPeridoSeguimientoDocumento
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPeriodoSeguimientoDocumento>> {
    return this.find<IProyectoPeriodoSeguimientoDocumentoBackend, IProyectoPeriodoSeguimientoDocumento>(
      `${this.endpointUrl}/${id}/proyectoperiodoseguimientodocumentos`,
      options,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER
    );
  }

  /**
   * Comprueba si existe documentos asociados al proyecto periodo seguimiento
   *
   * @param id Id del proyecto periodo seguimiento
   * @retrurn true/false
   */
  existsDocumentos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectoperiodoseguimientodocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  updateFechaPresentacionDocumentacion(periodoSeguimiento: IProyectoPeriodoSeguimiento) {
    const url = `${this.endpointUrl}/${periodoSeguimiento.id}/presentacion-documentacion`;
    return this.http.patch<IProyectoPeriodoSeguimientoBackend>(
      url,
      PROYECTO_PERIODO_SEGUIMIENTO_PRESENTACION_DOCUMENTACION_REQUEST_CONVERTER.fromTarget(periodoSeguimiento),
    ).pipe(
      map(response => PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER.toTarget(response))
    );
  }
}
