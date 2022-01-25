import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IPeriodoAmortizacionRequest } from './periodo-amortizacion-request';
import { PERIODO_AMORTIZACION_REQUEST_CONVERTER } from './periodo-amortizacion-request.converter';

// tslint:disable-next-line: variable-name
const _PeriodoAmortizacionServiceMixinBase:
  CreateCtor<IProyectoPeriodoAmortizacion, IProyectoPeriodoAmortizacion, IPeriodoAmortizacionRequest, IPeriodoAmortizacionRequest> &
  UpdateCtor<number, IProyectoPeriodoAmortizacion, IProyectoPeriodoAmortizacion, IPeriodoAmortizacionRequest, IPeriodoAmortizacionRequest> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PERIODO_AMORTIZACION_REQUEST_CONVERTER,
      PERIODO_AMORTIZACION_REQUEST_CONVERTER
    ),
    PERIODO_AMORTIZACION_REQUEST_CONVERTER,
    PERIODO_AMORTIZACION_REQUEST_CONVERTER
  );

@Injectable({ providedIn: 'root' })
export class PeriodoAmortizacionService extends _PeriodoAmortizacionServiceMixinBase {

  private static readonly MAPPING = '/periodos-amortizacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${PeriodoAmortizacionService.MAPPING}`,
      http
    );
  }

  /**
   * Elimina un periodo de amortizacion por id.
   *
   * @param id Id del proyecto periodo amortizacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
