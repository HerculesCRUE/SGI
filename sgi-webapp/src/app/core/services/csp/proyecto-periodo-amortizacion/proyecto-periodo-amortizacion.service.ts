import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  mixinCreate,
  mixinFindAll,
  mixinUpdate,
  RSQLSgiRestFilter,
  SgiRestBaseService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProyectoPeriodoAmortizacionRequest } from './proyecto-periodo-amortizacion-request';
import { PROYECTO_PERIODO_AMORTIZACION_REQUEST_CONVERTER } from './proyecto-periodo-amortizacion-request.converter';
import { IProyectoPeriodoAmortizacionResponse } from './proyecto-periodo-amortizacion-response';
import { PROYECTO_PERIODO_AMORTIZACION_RESPONSE_CONVERTER } from './proyecto-periodo-amortizacion-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoPeriodoAmortizacionServiceMixinBase:
  FindAllCtor<IProyectoPeriodoAmortizacion, IProyectoPeriodoAmortizacionResponse> &
  CreateCtor<
    IProyectoPeriodoAmortizacion,
    IProyectoPeriodoAmortizacion,
    IProyectoPeriodoAmortizacionRequest,
    IProyectoPeriodoAmortizacionRequest
  > &
  UpdateCtor<
    number,
    IProyectoPeriodoAmortizacion,
    IProyectoPeriodoAmortizacion,
    IProyectoPeriodoAmortizacionRequest,
    IProyectoPeriodoAmortizacionRequest
  > &
  typeof SgiRestBaseService =
  mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        PROYECTO_PERIODO_AMORTIZACION_REQUEST_CONVERTER,
        PROYECTO_PERIODO_AMORTIZACION_RESPONSE_CONVERTER
      ),
      PROYECTO_PERIODO_AMORTIZACION_REQUEST_CONVERTER,
      PROYECTO_PERIODO_AMORTIZACION_RESPONSE_CONVERTER
    ),
    PROYECTO_PERIODO_AMORTIZACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoAmortizacionService
  extends _ProyectoPeriodoAmortizacionServiceMixinBase {
  private static readonly MAPPING = '/proyectosperiodosamortizacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPeriodoAmortizacionService.MAPPING}`,
      http
    );
  }

  findByproyectoId(proyectoId: number): Observable<SgiRestListResult<IProyectoPeriodoAmortizacion>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoAnualidad.proyecto.id', SgiRestFilterOperator.EQUALS, proyectoId.toString())
    };

    return this.findAll(options);
  }

  /**
   * Elimina un proyecto periodo amortizacion por id.
   *
   * @param id Id del proyecto periodo amortizacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
