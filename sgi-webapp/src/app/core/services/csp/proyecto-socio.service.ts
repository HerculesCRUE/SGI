import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-pago.converter';
import { IProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-pago-backend';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoSocioEquipoResponse } from './proyecto-socio-equipo/proyecto-socio-equipo-response';
import { PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER } from './proyecto-socio-equipo/proyecto-socio-equipo.converter';
import { IProyectoSocioPeriodoJustificacionResponse } from './proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-response';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from './proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-response.converter';
import { IProyectoSocioResponse } from './proyecto-socio/proyecto-socio-response';
import { PROYECTO_SOCIO_RESPONSE_CONVERTER } from './proyecto-socio/proyecto-socio.converter';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioService extends SgiMutableRestService<number, IProyectoSocioResponse, IProyectoSocio> {

  private static readonly MAPPING = '/proyectosocios';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioService.name,
      `${environment.serviceServers.csp}${ProyectoSocioService.MAPPING}`,
      http,
      PROYECTO_SOCIO_RESPONSE_CONVERTER
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
    return this.find<IProyectoSocioEquipoResponse, IProyectoSocioEquipo>(
      `${this.endpointUrl}/${id}/proyectosocioequipos`,
      options,
      PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER
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
    return this.find<IProyectoSocioPeriodoJustificacionResponse, IProyectoSocioPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/proyectosocioperiodojustificaciones`,
      options,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
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
