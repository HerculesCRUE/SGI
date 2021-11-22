import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { TIPO_FACTURACION_REQUEST_CONVERTER } from './tipo-facturacion-request.converter';
import { TIPO_FACTURACION_RESPONSE_CONVERTER } from './tipo-facturacion-response.converter';
import { ITipoFacturacionRequest } from './tipo-facturacion-request';
import { ITipoFacturacionResponse } from './tipo-facturacion-response';

// tslint:disable-next-line: variable-name
const _TipoFacturacionServiceMixinBase: FindAllCtor<ITipoFacturacion, ITipoFacturacionResponse> &
  CreateCtor<ITipoFacturacion, ITipoFacturacion, ITipoFacturacionRequest, ITipoFacturacionRequest> &
  UpdateCtor<number, ITipoFacturacion, ITipoFacturacion, ITipoFacturacionRequest, ITipoFacturacionRequest> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_FACTURACION_REQUEST_CONVERTER,
        TIPO_FACTURACION_RESPONSE_CONVERTER
      ),
      TIPO_FACTURACION_REQUEST_CONVERTER,
      TIPO_FACTURACION_RESPONSE_CONVERTER
    ),
    TIPO_FACTURACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoFacturacionService extends _TipoFacturacionServiceMixinBase {

  private static readonly MAPPING = '/tiposfacturacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoFacturacionService.MAPPING}`,
      http,
    );
  }
}
