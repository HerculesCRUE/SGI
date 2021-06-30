import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-justificacion-documento.converter';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-justificacion.converter';
import { IProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-backend';
import { IProyectoSocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-documento-backend';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionService
  extends SgiMutableRestService<number, IProyectoSocioPeriodoJustificacionBackend, IProyectoSocioPeriodoJustificacion> {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoJustificacionService.name,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: IProyectoSocioPeriodoJustificacion[]):
    Observable<IProyectoSocioPeriodoJustificacion[]> {
    return this.http.patch<IProyectoSocioPeriodoJustificacionBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

  /**
   * Devuelve el listado de IProyectoSocioPeriodoJustificacionDocumento de un IProyectoSocioPeriodoJustificacion
   *
   * @param id Id del IProyectoSocioPeriodoJustificacion
   */
  findAllProyectoSocioPeriodoJustificacionDocumento(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoSocioPeriodoJustificacionDocumento>> {
    return this.find<IProyectoSocioPeriodoJustificacionDocumentoBackend, IProyectoSocioPeriodoJustificacionDocumento>(
      `${this.endpointUrl}/${id}/proyectosocioperiodojustificaciondocumentos`,
      options,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
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
}
