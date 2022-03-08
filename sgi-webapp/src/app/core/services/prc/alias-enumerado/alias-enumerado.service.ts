import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAliasEnumerado } from '@core/models/prc/alias-enumerado';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _AliasEnumeradoServiceMixinBase:
  FindAllCtor<IAliasEnumerado, IAliasEnumerado> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class AliasEnumeradoService extends _AliasEnumeradoServiceMixinBase {

  private static readonly MAPPING = '/alias-enumerados';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${AliasEnumeradoService.MAPPING}`,
      http,
    );
  }
}
