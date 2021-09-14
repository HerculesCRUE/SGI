import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPais } from '@core/models/sgo/pais';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _PaisServiceMixinBase:
  FindByIdCtor<string, IPais, IPais> &
  FindAllCtor<IPais, IPais> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class PaisService extends _PaisServiceMixinBase {
  private static readonly MAPPING = '/paises';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${PaisService.MAPPING}`,
      http
    );
  }

}
