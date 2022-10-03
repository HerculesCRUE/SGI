import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IAlegacionRequerimientoRequest } from './alegacion-requerimiento-request';
import { ALEGACION_REQUERIMIENTO_REQUEST_CONVERTER } from './alegacion-requerimiento-request.converter';
import { IAlegacionRequerimientoResponse } from './alegacion-requerimiento-response';
import { ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER } from './alegacion-requerimiento-response.converter';

// tslint:disable-next-line: variable-name
const _AlegacionRequerimientoMixinBase:
  CreateCtor<IAlegacionRequerimiento, IAlegacionRequerimiento,
    IAlegacionRequerimientoRequest, IAlegacionRequerimientoResponse> &
  UpdateCtor<number, IAlegacionRequerimiento, IAlegacionRequerimiento,
    IAlegacionRequerimientoRequest, IAlegacionRequerimientoResponse> &
  typeof SgiRestBaseService =
  mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      ALEGACION_REQUERIMIENTO_REQUEST_CONVERTER,
      ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER
    ),
    ALEGACION_REQUERIMIENTO_REQUEST_CONVERTER,
    ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AlegacionRequerimientoService extends _AlegacionRequerimientoMixinBase {

  private static readonly MAPPING = '/alegaciones-requerimiento';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AlegacionRequerimientoService.MAPPING}`,
      http
    );
  }
}
