import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPais } from '@core/models/sgo/pais';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _PaisServiceMixinBase:
  FindAllCtor<IPais, IPais> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class PaisPublicService extends _PaisServiceMixinBase {
  private static readonly MAPPING = '/paises';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${PaisPublicService.PUBLIC_PREFIX}${PaisPublicService.MAPPING}`,
      http
    );
  }

}
