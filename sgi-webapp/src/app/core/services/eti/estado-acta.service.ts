import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEstadoActa } from '@core/models/eti/estado-acta';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IEstadoActaResponse } from './estado-acta/estado-acta-response';
import { ESTADO_ACTA_RESPONSE_CONVERTER } from './estado-acta/estado-acta-response.converter';

const _EstadoActaServiceMixinBase:
  CreateCtor<IEstadoActa, IEstadoActa, IEstadoActaResponse, IEstadoActaResponse> &
  UpdateCtor<number, IEstadoActa, IEstadoActa, IEstadoActaResponse, IEstadoActaResponse> &
  FindByIdCtor<number, IEstadoActa, IEstadoActaResponse> &
  FindAllCtor<IEstadoActa, IEstadoActaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          ESTADO_ACTA_RESPONSE_CONVERTER,
          ESTADO_ACTA_RESPONSE_CONVERTER
        ),
        ESTADO_ACTA_RESPONSE_CONVERTER,
        ESTADO_ACTA_RESPONSE_CONVERTER
      ),
      ESTADO_ACTA_RESPONSE_CONVERTER
    ),
    ESTADO_ACTA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EstadoActaService extends _EstadoActaServiceMixinBase {
  private static readonly MAPPING = '/estadoactas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${EstadoActaService.MAPPING}`,
      http
    );
  }
}
