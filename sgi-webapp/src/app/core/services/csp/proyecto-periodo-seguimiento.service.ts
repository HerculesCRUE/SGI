import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoPeriodoSeguimientoDocumentoResponse } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response.converter';
import { PROYECTO_PERIODO_SEGUIMIENTO_PRESENTACION_DOCUMENTACION_REQUEST_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-presentacion-documentacion-request.converter';
import { IProyectoPeriodoSeguimientoResponse } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-response';
import { PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-response.converter';

const _ProyectoPeriodoSeguimientoServiceMixinBase:
  CreateCtor<IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimientoResponse, IProyectoPeriodoSeguimientoResponse> &
  UpdateCtor<number, IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimientoResponse, IProyectoPeriodoSeguimientoResponse> &
  FindAllCtor<IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimientoResponse> &
  FindByIdCtor<number, IProyectoPeriodoSeguimiento, IProyectoPeriodoSeguimientoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinFindAll(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER,
          PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER
        ),
        PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER,
        PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER
      ),
      PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoService extends _ProyectoPeriodoSeguimientoServiceMixinBase {
  private static readonly MAPPING = '/proyectoperiodoseguimientos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoService.MAPPING}`,
      http
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
    return this.find<IProyectoPeriodoSeguimientoDocumentoResponse, IProyectoPeriodoSeguimientoDocumento>(
      `${this.endpointUrl}/${id}/proyectoperiodoseguimientodocumentos`,
      options,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
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
    return this.http.patch<IProyectoPeriodoSeguimientoResponse>(
      url,
      PROYECTO_PERIODO_SEGUIMIENTO_PRESENTACION_DOCUMENTACION_REQUEST_CONVERTER.fromTarget(periodoSeguimiento),
    ).pipe(
      map(response => PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
