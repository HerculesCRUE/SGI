import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate,
  mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { IRepartoIngresoRequest } from './reparto-ingreso-request';
import { REPARTO_INGRESO_REQUEST_CONVERTER } from './reparto-ingreso-request.converter';
import { IRepartoIngresoResponse } from './reparto-ingreso-response';
import { REPARTO_INGRESO_RESPONSE_CONVERTER } from './reparto-ingreso-response.converter';

// tslint:disable-next-line: variable-name
const _RepartoIngresoServiceMixinBase:
  FindAllCtor<IRepartoIngreso, IRepartoIngresoResponse> &
  FindByIdCtor<number, IRepartoIngreso, IRepartoIngresoResponse> &
  CreateCtor<IRepartoIngreso, IRepartoIngreso, IRepartoIngresoRequest, IRepartoIngresoResponse> &
  UpdateCtor<number, IRepartoIngreso, IRepartoIngreso, IRepartoIngresoRequest, IRepartoIngresoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          REPARTO_INGRESO_REQUEST_CONVERTER,
          REPARTO_INGRESO_RESPONSE_CONVERTER
        ),
        REPARTO_INGRESO_REQUEST_CONVERTER,
        REPARTO_INGRESO_RESPONSE_CONVERTER
      ),
      REPARTO_INGRESO_RESPONSE_CONVERTER
    ),
    REPARTO_INGRESO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RepartoIngresoService extends _RepartoIngresoServiceMixinBase {

  private static readonly MAPPING = '/repartoingresos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${RepartoIngresoService.MAPPING}`,
      http,
    );
  }
}
