import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_EQUIPO_CONVERTER } from '@core/converters/csp/proyecto-socio-equipo.converter';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-justificacion.converter';
import { PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-pago.converter';
import { PROYECTO_SOCIO_CONVERTER } from '@core/converters/csp/proyecto-socio.converter';
import { IProyectoSocioBackend } from '@core/models/csp/backend/proyecto-socio-backend';
import { IProyectoSocioEquipoBackend } from '@core/models/csp/backend/proyecto-socio-equipo-backend';
import { IProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-backend';
import { IProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-pago-backend';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioService extends SgiMutableRestService<number, IProyectoSocioBackend, IProyectoSocio>  {
  private static readonly MAPPING = '/proyectosocios';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioService.name,
      `${environment.serviceServers.csp}${ProyectoSocioService.MAPPING}`,
      http,
      PROYECTO_SOCIO_CONVERTER
    );
  }

  /**
   * Comprueba si existe un proyecto socio
   *
   * @param id Id del proyecto socio
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Devuelve el listado de IProyectoSocioEquipo de un IProyectoSocio
   *
   * @param id Id del IProyectoSocio
   */
  findAllProyectoEquipoSocio(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoSocioEquipo>> {
    return this.find<IProyectoSocioEquipoBackend, IProyectoSocioEquipo>(
      `${this.endpointUrl}/${id}/proyectosocioequipos`,
      options,
      PROYECTO_SOCIO_EQUIPO_CONVERTER
    );
  }

  /**
   * Devuelve el listado de IProyectoSocioPeriodoPago de un IProyectoSocio
   *
   * @param id Id del proyecto socio
   */
  findAllProyectoSocioPeriodoPago(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoSocioPeriodoPago>> {
    return this.find<IProyectoSocioPeriodoPagoBackend, IProyectoSocioPeriodoPago>(
      `${this.endpointUrl}/${id}/proyectosocioperiodopagos`,
      options, PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER
    );
  }

  /**
   * Devuelve el listado de IProyectoSocioPeriodoJustificacion de un IProyectoSocio
   *
   * @param id Id del proyecto socio
   */
  findAllProyectoSocioPeriodoJustificacion(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoSocioPeriodoJustificacion>> {
    return this.find<IProyectoSocioPeriodoJustificacionBackend, IProyectoSocioPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/proyectosocioperiodojustificaciones`,
      options,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  /**
   * Comprueba si un IProyectoSocio tiene IproyectoSocioEquipo, IProyectoSocioPeriodoPago, SocioPeriodoJustificacionDocumento
   * y/o ProyectoSocioPeriodoJustificacion relacionados
   *
   *  @param id Id deL proyecto
   */
  vinculaciones(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/vinculaciones`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
