import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITramoRepartoRequest } from './tramo-reparto-request';
import { TRAMO_REPARTO_REQUEST_CONVERTER } from './tramo-reparto-request.converter';


// tslint:disable-next-line: variable-name
const _TramoRepartoServiceMixinBase:
  FindAllCtor<ITramoReparto, ITramoReparto> &
  CreateCtor<ITramoReparto, ITramoReparto, ITramoRepartoRequest, ITramoReparto> &
  UpdateCtor<number, ITramoReparto, ITramoReparto, ITramoRepartoRequest, ITramoReparto> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TRAMO_REPARTO_REQUEST_CONVERTER,
      ),
      TRAMO_REPARTO_REQUEST_CONVERTER
    )
  );

@Injectable({
  providedIn: 'root'
})
export class TramoRepartoService extends _TramoRepartoServiceMixinBase {
  private static readonly MAPPING = '/tramosreparto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${TramoRepartoService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITramoReparto>> {
    return this.find<ITramoReparto, ITramoReparto>(
      `${this.endpointUrl}/todos`,
      options);
  }

  /**
   * Activar el tramo de reparto
   * @param id id del tramo de reparto.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar el tramo de reparto
   * @param options id del tramo de reparto.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }
}
