import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { environment } from '@env';
import {
  FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoClasificacionPublic } from 'src/app/esb/sgo/shared/clasificacion-public-modal/clasificacion-public-modal.component';

// tslint:disable-next-line: variable-name
const _ClasificacionServiceMixinBase:
  FindByIdCtor<string, IClasificacion, IClasificacion> &
  FindAllCtor<IClasificacion, IClasificacion> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService
    )
  );

@Injectable({
  providedIn: 'root'
})
export class ClasificacionPublicService extends _ClasificacionServiceMixinBase {
  private static readonly MAPPING = '/clasificaciones';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${ClasificacionPublicService.PUBLIC_PREFIX}${ClasificacionPublicService.MAPPING}`,
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
  findAllPadres(tipoClasificacion?: TipoClasificacionPublic): Observable<SgiRestListResult<IClasificacion>> {
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
