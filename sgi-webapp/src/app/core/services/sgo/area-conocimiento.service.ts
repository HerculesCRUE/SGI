import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AreaConocimientoService extends SgiRestService<string, IAreaConocimiento>{
  private static readonly MAPPING = '/areas-conocimiento';

  constructor(protected http: HttpClient) {
    super(
      AreaConocimientoService.name,
      `${environment.serviceServers.sgo}${AreaConocimientoService.MAPPING}`,
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
   * Busca todas las areas de conocimiento que tengan como padre alguno de los ids indicados.
   *
   * @param ids lista de identificadores de IAreaConocimiento
   * @returns la lista de IAreaConocimiento
   */
  findAllHijos(ids: string | string[]): Observable<SgiRestListResult<IAreaConocimiento>> {
    let options: SgiRestFindOptions;
    if (Array.isArray(ids)) {
      options = {
        filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.IN, ids)
      };
    } else {
      options = {
        filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.EQUALS, ids)
      };
    }

    return this.findAll(options);
  }

}
