import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { IFuenteFinanciacionRequest } from '@core/services/csp/fuente-financiacion/fuente-financiacion-request';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { SECTOR_APLICACION_REQUEST_CONVERTER } from './sector-aplicacion-request.converter';

// tslint:disable-next-line: variable-name
const _SectorAplicacionServiceMixinBase:
  FindAllCtor<ISectorAplicacion, ISectorAplicacion> &
  CreateCtor<ISectorAplicacion, ISectorAplicacion, IFuenteFinanciacionRequest, ISectorAplicacion> &
  UpdateCtor<number, ISectorAplicacion, ISectorAplicacion, IFuenteFinanciacionRequest, ISectorAplicacion> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SECTOR_APLICACION_REQUEST_CONVERTER,
      ),
      SECTOR_APLICACION_REQUEST_CONVERTER
    )
  );

@Injectable({
  providedIn: 'root'
})
export class SectorAplicacionService extends _SectorAplicacionServiceMixinBase {
  private static readonly MAPPING = '/sectoresaplicacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${SectorAplicacionService.MAPPING}`,
      http,
    );
  }

  /**
 * Muestra activos y no activos
 *
 * @param options opciones de búsqueda.
 */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ISectorAplicacion>> {
    return this.find<ISectorAplicacion, ISectorAplicacion>(
      `${this.endpointUrl}/todos`,
      options);
  }

  /**
   * Activar el sector de aplicación
   * @param id id del sector de aplicación.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar el sector de aplicación
   * @param options id del sector de aplicación.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }
}
