import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IComiteEditorial } from '@core/models/prc/comite-editorial';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IComiteEditorialResponse } from './comite-editorial-response';
import { COMITE_EDITORIAL_RESPONSE_CONVERTER } from './comite-editorial-response.converter';

// tslint:disable-next-line: variable-name
const _ComiteEditorialMixinBase:
  FindAllCtor<IComiteEditorial, IComiteEditorialResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    COMITE_EDITORIAL_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ComiteEditorialService extends _ComiteEditorialMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/comites-editoriales';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ComiteEditorialService.MAPPING}`,
      http,
    );
  }
}
