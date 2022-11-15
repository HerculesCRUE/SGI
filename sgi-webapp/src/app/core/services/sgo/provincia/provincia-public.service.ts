import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProvincia } from '@core/models/sgo/provincia';
import { environment } from '@env';
import {
  FindAllCtor, mixinFindAll, RSQLSgiRestFilter,
  RSQLSgiRestSort,
  SgiRestBaseService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';

// tslint:disable-next-line: variable-name
const _ProvinciaServiceMixinBase:
  FindAllCtor<IProvincia, IProvincia> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class ProvinciaPublicService extends _ProvinciaServiceMixinBase {
  private static readonly MAPPING = '/provincias';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${ProvinciaPublicService.PUBLIC_PREFIX}${ProvinciaPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Busca todas las provincias de una determinada comunidad
   *
   * @param comunidadAutonomaId Id de comunidad autonoma
   * @returns la lista de IProvincia.
   */
  findByComunidadAutonomaId(comunidadAutonomaId: string): Observable<SgiRestListResult<IProvincia>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('comunidadAutonomaId', SgiRestFilterOperator.EQUALS, comunidadAutonomaId),
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.findAll(options);
  }

}
