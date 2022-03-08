import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPublicacion } from '@core/models/prc/publicacion';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IPublicacionResponse } from './publicacion-response';
import { PUBLICACION_RESPONSE_CONVERTER } from './publicacion-response.converter';

// tslint:disable-next-line: variable-name
const _PublicacionServiceMixinBase:
  FindAllCtor<IPublicacion, IPublicacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    PUBLICACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class PublicacionService extends _PublicacionServiceMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/publicaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${PublicacionService.MAPPING}`,
      http,
    );
  }
}
