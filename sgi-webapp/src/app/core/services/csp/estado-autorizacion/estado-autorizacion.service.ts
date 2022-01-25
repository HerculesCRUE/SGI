import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IEstadoAutorizacionRequest } from './estado-autorizacion-request';
import { ESTADO_AUTORIZACION_REQUEST_CONVERTER } from './estado-autorizacion-request.converter';
import { IEstadoAutorizacionResponse } from './estado-autorizacion-response';
import { ESTADO_AUTORIZACION_RESPONSE_CONVERTER } from './estado-autorizacion-response.converter';


// tslint:disable-next-line: variable-name
const _EstadoAutorizacionMixinBase:
  CreateCtor<IEstadoAutorizacion, IEstadoAutorizacion, IEstadoAutorizacionRequest, IEstadoAutorizacionResponse> &
  UpdateCtor<number, IEstadoAutorizacion, IEstadoAutorizacion, IEstadoAutorizacionRequest, IEstadoAutorizacionResponse> &
  FindByIdCtor<number, IEstadoAutorizacion, IEstadoAutorizacionResponse> &
  FindAllCtor<IEstadoAutorizacion, IEstadoAutorizacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          ESTADO_AUTORIZACION_REQUEST_CONVERTER,
          ESTADO_AUTORIZACION_RESPONSE_CONVERTER
        ),
        ESTADO_AUTORIZACION_REQUEST_CONVERTER,
        ESTADO_AUTORIZACION_RESPONSE_CONVERTER
      ),
      ESTADO_AUTORIZACION_RESPONSE_CONVERTER
    ),
    ESTADO_AUTORIZACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EstadoAutorizacionService extends _EstadoAutorizacionMixinBase {
  private static readonly MAPPING = '/estadosautorizaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${EstadoAutorizacionService.MAPPING}`,
      http,
    );
  }

}
