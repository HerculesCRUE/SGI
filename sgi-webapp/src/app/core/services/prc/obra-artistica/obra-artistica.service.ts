import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IObraArtistica } from '@core/models/prc/obra-artistica';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IObraArtisticaResponse } from './obra-artistica-response';
import { OBRA_ARTISTICA_RESPONSE_CONVERTER } from './obra-artistica-response.converter';

// tslint:disable-next-line: variable-name
const _ObraArtisticaMixinBase:
  FindAllCtor<IObraArtistica, IObraArtisticaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    OBRA_ARTISTICA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ObraArtisticaService extends _ObraArtisticaMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/obras-artisticas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ObraArtisticaService.MAPPING}`,
      http,
    );
  }
}
