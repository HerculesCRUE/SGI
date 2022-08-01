import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { environment } from '@env';
import { SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProyectoPeriodoJustificacionResponse } from '../proyecto-periodo-justificacion/proyecto-periodo-justificacion-response';
import { PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from '../proyecto-periodo-justificacion/proyecto-periodo-justificacion-response.converter';
import { IRequerimientoJustificacionResponse } from '../requerimiento-justificacion/requerimiento-justificacion-response';
import { REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from '../requerimiento-justificacion/requerimiento-justificacion-response.converter';
import { IProyectoSeguimientoEjecucionEconomicaResponse } from './proyecto-seguimiento-ejecucion-economica-response';
import { PROYECTO_SEGUIMIENTO_EJECUCION_ECONOMICA_RESPONSE_CONVERTER } from './proyecto-seguimiento-ejecucion-economica-response.converter';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSeguimientoEjecucionEconomicaService extends SgiRestBaseService {
  private static readonly MAPPING = '/seguimiento-ejecucion-economica';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoSeguimientoEjecucionEconomicaService.MAPPING}`,
      http
    );
  }

  findProyectosSeguimientoEjecucionEconomica(
    proyectoSgeRef: string,
    options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoSeguimientoEjecucionEconomica>> {
    return this.find<IProyectoSeguimientoEjecucionEconomicaResponse, IProyectoSeguimientoEjecucionEconomica>(
      `${this.endpointUrl}/${proyectoSgeRef}/proyectos`,
      options,
      PROYECTO_SEGUIMIENTO_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  findProyectoPeriodosJustificacion(
    proyectoSgeRef: string,
    options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPeriodoJustificacion>> {
    return this.find<IProyectoPeriodoJustificacionResponse, IProyectoPeriodoJustificacion>(
      `${this.endpointUrl}/${proyectoSgeRef}/periodos-justificacion`,
      options,
      PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
    );
  }

  findRequerimientosJustificacion(
    proyectoSgeRef: string,
    options?: SgiRestFindOptions): Observable<SgiRestListResult<IRequerimientoJustificacion>> {
    return this.find<IRequerimientoJustificacionResponse, IRequerimientoJustificacion>(
      `${this.endpointUrl}/${proyectoSgeRef}/requerimientos-justificacion`,
      options,
      REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
    );
  }
}
