import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IViaProteccionRequest } from './via-proteccion-request';
import { VIA_PROTECCION_REQUEST_CONVERTER } from './via-proteccion-request.converter';
import { IViaProteccionResponse } from './via-proteccion-response';
import { VIA_PROTECCION_RESPONSE_CONVERTER } from './via-proteccion-response.converter';

// tslint:disable-next-line: variable-name
const _ViaProteccionServiceMixinBase:
  CreateCtor<IViaProteccion, IViaProteccion, IViaProteccionRequest, IViaProteccionResponse> &
  UpdateCtor<number, IViaProteccion, IViaProteccion, IViaProteccionRequest, IViaProteccionResponse> &
  FindByIdCtor<number, IViaProteccion, IViaProteccionResponse> &
  FindAllCtor<IViaProteccion, IViaProteccionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          VIA_PROTECCION_REQUEST_CONVERTER,
          VIA_PROTECCION_RESPONSE_CONVERTER
        ),
        VIA_PROTECCION_REQUEST_CONVERTER,
        VIA_PROTECCION_RESPONSE_CONVERTER
      ),
      VIA_PROTECCION_RESPONSE_CONVERTER
    ),
    VIA_PROTECCION_RESPONSE_CONVERTER
  );
@Injectable({
  providedIn: 'root'
})
export class ViaProteccionService extends _ViaProteccionServiceMixinBase {

  private static readonly MAPPING = '/viasproteccion';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${ViaProteccionService.MAPPING}`, http);
  }

  /**
   * Obtine objetos del tipo ViaProteccion activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IViaProteccion>> {
    return this.find<IViaProteccionResponse, IViaProteccion>(
      `${this.endpointUrl}/todos`,
      options,
      VIA_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar un Tipo de Proteccion
   * @param id id del tipo a activar
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, {});
  }

  /**
   * Desactivar Via de Proteccion
   * @param id id del tipo a activar
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, {});
  }
}
