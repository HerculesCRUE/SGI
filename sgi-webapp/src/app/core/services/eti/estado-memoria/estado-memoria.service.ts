import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@sgi/framework/http';
import { ESTADO_MEMORIA_RESPONSE_CONVERTER } from './estado-memoria-response.converter';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { IEstadoMemoriaResponse } from './estado-memoria-response';

// tslint:disable-next-line: variable-name
const _EstadoMemoriaMixinBase:
  FindByIdCtor<number, IEstadoMemoria, IEstadoMemoriaResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    ESTADO_MEMORIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EstadoMemoriaService extends _EstadoMemoriaMixinBase {
  private static readonly MAPPING = '/estadomemorias';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EstadoMemoriaService.MAPPING}`,
      http,
    );
  }

}
