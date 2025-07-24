import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from '@core/services/csp/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-response.converter';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoSocioPeriodoJustificacionDocumentoResponse } from './proyecto-socio-periodo-justificacion-documento/proyecto-socio-periodo-justificacion-documento-response';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER } from './proyecto-socio-periodo-justificacion-documento/proyecto-socio-periodo-justificacion-documento-response.converter';
import { IProyectoSocioPeriodoJustificacionResponse } from './proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-response';

const _ProyectoSocioPeriodoJustificacionServiceMixinBase:
  CreateCtor<IProyectoSocioPeriodoJustificacion, IProyectoSocioPeriodoJustificacion, IProyectoSocioPeriodoJustificacionResponse, IProyectoSocioPeriodoJustificacionResponse> &
  UpdateCtor<number, IProyectoSocioPeriodoJustificacion, IProyectoSocioPeriodoJustificacion, IProyectoSocioPeriodoJustificacionResponse, IProyectoSocioPeriodoJustificacionResponse> &
  FindByIdCtor<number, IProyectoSocioPeriodoJustificacion, IProyectoSocioPeriodoJustificacionResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER,
        PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
      ),
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
    ),
    PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionService
  extends _ProyectoSocioPeriodoJustificacionServiceMixinBase {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: IProyectoSocioPeriodoJustificacion[]):
    Observable<IProyectoSocioPeriodoJustificacion[]> {
    return this.http.patch<IProyectoSocioPeriodoJustificacionResponse[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Devuelve el listado de IProyectoSocioPeriodoJustificacionDocumento de un IProyectoSocioPeriodoJustificacion
   *
   * @param id Id del IProyectoSocioPeriodoJustificacion
   */
  findAllProyectoSocioPeriodoJustificacionDocumento(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoSocioPeriodoJustificacionDocumento>> {
    return this.find<IProyectoSocioPeriodoJustificacionDocumentoResponse, IProyectoSocioPeriodoJustificacionDocumento>(
      `${this.endpointUrl}/${id}/proyectosocioperiodojustificaciondocumentos`,
      options,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existe la entidad con el identificador facilitadao
   *
   * @param id Identificador de la entidad
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si existen documentos asociados a la entidad
   *
   * @param id Identificador de la entidad
   */
  existsDocumentos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/documentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
