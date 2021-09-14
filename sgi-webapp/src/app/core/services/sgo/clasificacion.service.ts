import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { TipoClasificacion } from 'src/app/esb/sgo/shared/clasificacion-modal/clasificacion-modal.component';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClasificacionService extends SgiRestService<string, IClasificacion>{
  private static readonly MAPPING = '/clasificaciones';

  constructor(protected http: HttpClient) {
    super(
      ClasificacionService.name,
      `${environment.serviceServers.sgo}${ClasificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Busca todas las clasificaciones de primer nivel si no se indica ningun tipo
   * o las del tipo concreto en caso contrario.
   *
   * @param tipoClasificacion (opcional) tipo de clasificacion.
   * @returns la lista de IClasificacion.
   */
  findAllPadres(tipoClasificacion?: TipoClasificacion): Observable<SgiRestListResult<IClasificacion>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.IS_NULL, '')
    };

    if (tipoClasificacion) {
      options.filter.and('tipoClasificacion', SgiRestFilterOperator.EQUALS, tipoClasificacion);
    }

    return this.findAll(options);
  }

  /**
   * Busca todas las clasificaciones que tengan como padre el id indicado.
   *
   * @param id identificador de IClasificacion.
   * @returns la lista de IClasificacion.
   */
  findAllHijos(id: string): Observable<SgiRestListResult<IClasificacion>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.EQUALS, id)
    };

    return this.findAll(options);
  }

}
