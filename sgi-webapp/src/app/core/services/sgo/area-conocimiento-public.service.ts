import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, mixinFindAll, mixinFindById } from '@sgi/framework/http';
import { Observable } from 'rxjs';

// tslint:disable-next-line: variable-name
const _AreaConocimientoServiceMixinBase:
  FindByIdCtor<string, IAreaConocimiento, IAreaConocimiento> &
  FindAllCtor<IAreaConocimiento, IAreaConocimiento> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService
    )
  );

@Injectable({
  providedIn: 'root'
})
export class AreaConocimientoPublicService extends _AreaConocimientoServiceMixinBase {
  private static readonly MAPPING = '/areas-conocimiento';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${AreaConocimientoPublicService.PUBLIC_PREFIX}${AreaConocimientoPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Busca todas las areas de conocimiento de primer nivel (ramas de conocimiento).
   *
   * @returns la lista de IAreaConocimiento
   */
  findAllRamasConocimiento(): Observable<SgiRestListResult<IAreaConocimiento>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.IS_NULL, '')
    };

    return this.findAll(options);
  }

  /**
   * Busca todas las areas de conocimiento que tengan como padre el id indicado.
   *
   * @param id identificador de IAreaConocimiento
   * @returns la lista de IAreaConocimiento
   */
  findAllHijos(id: string): Observable<SgiRestListResult<IAreaConocimiento>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.EQUALS, id)
    };

    return this.findAll(options);
  }

}
