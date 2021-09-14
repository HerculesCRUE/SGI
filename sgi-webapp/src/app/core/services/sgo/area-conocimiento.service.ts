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
