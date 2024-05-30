import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICentro } from '@core/models/sgo/centro';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _CentroServiceMixinBase:
  FindByIdCtor<string, ICentro, ICentro> &
  typeof SgiRestBaseService = mixinFindById(SgiRestBaseService);

@Injectable({
  providedIn: 'root'
})
export class CentroPublicService extends _CentroServiceMixinBase {
  private static readonly MAPPING = '/centros';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${CentroPublicService.PUBLIC_PREFIX}${CentroPublicService.MAPPING}`,
      http
    );
  }

}
