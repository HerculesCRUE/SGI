import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_SOCIO_EQUIPO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-equipo.converter';
import { SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-periodo-justificacion.converter';
import { SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-periodo-pago.converter';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio.converter';
import { ISolicitudProyectoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-backend';
import { ISolicitudProyectoSocioEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-equipo-backend';
import { ISolicitudProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-justificacion-backend';
import { ISolicitudProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-pago-backend';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { EmpresaService } from '../sgemp/empresa.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioService extends SgiMutableRestService<number, ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio>  {
  private static readonly MAPPING = '/solicitudproyectosocio';

  constructor(
    private readonly logger: NGXLogger,
    protected http: HttpClient,
    private empresaService: EmpresaService
  ) {
    super(
      SolicitudProyectoSocioService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_SOCIO_CONVERTER
    );
  }

  findById(id: number): Observable<ISolicitudProyectoSocio> {
    return super.findById(id).pipe(
      switchMap(solicitudProyectoSocio => {
        return this.empresaService.findById(solicitudProyectoSocio.empresa.id).pipe(
          map(empresa => {
            solicitudProyectoSocio.empresa = empresa;
            return solicitudProyectoSocio;
          }),
          catchError((err) => {
            this.logger.error(err);
            return of(solicitudProyectoSocio);
          })
        );
      })
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoSocioPeriodoPago de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoSocioPeriodoPago(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoSocioPeriodoPago>> {
    return this.find<ISolicitudProyectoSocioPeriodoPagoBackend, ISolicitudProyectoSocioPeriodoPago>(
      `${this.endpointUrl}/${id}/solicitudproyectosocioperiodopago`,
      options,
      SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoSocioPeriodoPago de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoSocioPeriodoJustificacion(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoSocioPeriodoJustificacion>> {
    return this.find<ISolicitudProyectoSocioPeriodoJustificacionBackend, ISolicitudProyectoSocioPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/solicitudproyectosocioperiodojustificaciones`,
      options,
      SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoSocioEquipo de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoSocioEquipo(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoSocioEquipo>> {
    return this.find<ISolicitudProyectoSocioEquipoBackend, ISolicitudProyectoSocioEquipo>(
      `${this.endpointUrl}/${id}/solicitudproyectosocioequipo`,
      options,
      SOLICITUD_PROYECTO_SOCIO_EQUIPO_CONVERTER
    );
  }

  /**
   * Comprueba si un ISolicitudProyectoSocio tiene ISolicitudProyectoSocioEquipo,
   * ISolicitudProyectoSocioPeriodoPago y/o ISolicitudProyectoSocioPeriodoJustificacion
   * relacionados
   *
   *  @param id Id deL proyecto
   */
  vinculaciones(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/vinculaciones`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
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
