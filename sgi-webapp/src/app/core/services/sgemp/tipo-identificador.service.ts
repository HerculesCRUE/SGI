import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoIdentificador } from '@core/models/sgemp/tipo-identificador';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _TipoIdentificadorServiceMixinBase:
  FindByIdCtor<string, ITipoIdentificador, ITipoIdentificador> &
  FindAllCtor<ITipoIdentificador, ITipoIdentificador> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class TipoIdentificadorService extends _TipoIdentificadorServiceMixinBase {
  private static readonly MAPPING = '/tipos-identificador';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgemp}${TipoIdentificadorService.MAPPING}`,
      http
    );
  }
}
