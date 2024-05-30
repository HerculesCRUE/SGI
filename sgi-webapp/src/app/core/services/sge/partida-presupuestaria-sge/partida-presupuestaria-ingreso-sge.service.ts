import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, SgiRestBaseService, mixinFindAll, mixinFindById } from '@sgi/framework/http';
import { IPartidaPresupuestariaSgeResponse } from './partida-presupuestaria-sge-response';
import { PARTIDA_PRESUPUESTARIA_SGE_RESPONSE_CONVERTER } from './partida-presupuestaria-sge-response.converter';

// tslint:disable-next-line: variable-name
const PartidaPresupuestariaSgeServiceMixinBase:
  FindByIdCtor<string, IPartidaPresupuestariaSge, IPartidaPresupuestariaSgeResponse> &
  FindAllCtor<IPartidaPresupuestariaSge, IPartidaPresupuestariaSgeResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService),
    PARTIDA_PRESUPUESTARIA_SGE_RESPONSE_CONVERTER
  );

@Injectable({ providedIn: 'root' })
export class PartidaPresupuestariaIngresoSgeService extends PartidaPresupuestariaSgeServiceMixinBase {

  private static readonly MAPPING = '/partidas-presupuestarias-ingresos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${PartidaPresupuestariaIngresoSgeService.MAPPING}`,
      http
    );
  }

}
