import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColectivo } from '@core/models/sgp/colectivo';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _ColectivoServiceMixinBase:
  FindByIdCtor<string, IColectivo, IColectivo> &
  FindAllCtor<IColectivo, IColectivo> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      null
    ),
    null
  );


@Injectable({
  providedIn: 'root'
})
export class ColectivoService extends _ColectivoServiceMixinBase {
  private static readonly MAPPING = '/colectivos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${ColectivoService.MAPPING}`,
      http,
    );
  }

}
