import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColectivo } from '@core/models/sgp/colectivo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _ColectivoMixinBase:
  FindByIdCtor<string, IColectivo, IColectivo> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class ColectivoPublicService extends _ColectivoMixinBase {
  private static readonly MAPPING = '/colectivos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${ColectivoPublicService.PUBLIC_PREFIX}${ColectivoPublicService.MAPPING}`,
      http
    );
  }

}
