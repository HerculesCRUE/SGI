import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@sgi/framework/http';
import { IPartidaPresupuestariaSgeResponse } from './partida-presupuestaria-sge-response';
import { PARTIDA_PRESUPUESTARIA_SGE_RESPONSE_CONVERTER } from './partida-presupuestaria-sge-response.converter';

// tslint:disable-next-line: variable-name
const PartidaPresupuestariaSgeServiceMixinBase:
  FindByIdCtor<string, IPartidaPresupuestariaSge, IPartidaPresupuestariaSgeResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    PARTIDA_PRESUPUESTARIA_SGE_RESPONSE_CONVERTER
  );

@Injectable({ providedIn: 'root' })
export class PartidaPresupuestariaIngresoSgePublicService extends PartidaPresupuestariaSgeServiceMixinBase {

  private static readonly MAPPING = '/partidas-presupuestarias-ingresos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${PartidaPresupuestariaIngresoSgePublicService.PUBLIC_PREFIX}${PartidaPresupuestariaIngresoSgePublicService.MAPPING}`,
      http
    );
  }

}
