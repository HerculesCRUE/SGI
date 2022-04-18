import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDireccionTesis } from '@core/models/prc/direccion-tesis';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IDireccionTesisResponse } from './direccion-tesis-response';
import { DIRECCION_TESIS_RESPONSE_CONVERTER } from './direccion-tesis-response.converter';

// tslint:disable-next-line: variable-name
const _DireccionTesisMixinBase:
  FindAllCtor<IDireccionTesis, IDireccionTesisResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    DIRECCION_TESIS_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DireccionTesisService extends _DireccionTesisMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/direcciones-tesis';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${DireccionTesisService.MAPPING}`,
      http,
    );
  }
}
