import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISexo } from '@core/models/sgp/sexo';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _SexoMixinBase:
  FindAllCtor<ISexo, ISexo> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class SexoPublicService extends _SexoMixinBase {
  private static readonly MAPPING = '/sexos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${SexoPublicService.PUBLIC_PREFIX}${SexoPublicService.MAPPING}`,
      http
    );
  }

}
