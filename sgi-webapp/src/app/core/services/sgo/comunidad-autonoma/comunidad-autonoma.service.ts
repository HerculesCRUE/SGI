import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IComunidadAutonoma } from '@core/models/sgo/comunidad-autonoma';
import { environment } from '@env';
import {
  FindAllCtor,
  FindByIdCtor,
  mixinFindAll,
  mixinFindById,
  RSQLSgiRestFilter,
  RSQLSgiRestSort,
  SgiRestBaseService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';

// tslint:disable-next-line: variable-name
const _ComunidadAutonomaServiceMixinBase:
  FindByIdCtor<string, IComunidadAutonoma, IComunidadAutonoma> &
  FindAllCtor<IComunidadAutonoma, IComunidadAutonoma> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class ComunidadAutonomaService extends _ComunidadAutonomaServiceMixinBase {
  private static readonly MAPPING = '/comunidades-autonomas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${ComunidadAutonomaService.MAPPING}`,
      http
    );
  }

  /**
   * Busca todas las comunidades de un determinado pais
   *
   * @param paisId Id de pais
   * @returns la lista de IComunidadAutonoma.
   */
  findByPaisId(paisId: string): Observable<SgiRestListResult<IComunidadAutonoma>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('paisId', SgiRestFilterOperator.EQUALS, paisId),
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.findAll(options);
  }

}
